package com.github.miachm.sods;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads {@code META-INF/manifest.xml} and collects per-file encryption metadata.
 */
class ManifestParser {
    private static final String MANIFEST_NS = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";
    private static final String LOEXT_NS = "urn:org:documentfoundation:names:experimental:office:xmlns:loext:1.0";

    private final Map<String, OdfEncryptionMetadata> encryptedEntries = new HashMap<>();

    static ManifestParser parse(byte[] manifestXml) throws IOException {
        ManifestParser parser = new ManifestParser();
        if (manifestXml == null || manifestXml.length == 0) {
            return parser;
        }
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(manifestXml));
            String currentPath = null;
            int currentSize = -1;
            OdfEncryptionMetadata.Builder builder = null;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String local = reader.getLocalName();
                    String ns = reader.getNamespaceURI();
                    if (isManifest(ns, local, "file-entry")) {
                        currentPath = attr(reader, "full-path");
                        currentSize = parseInt(attr(reader, "size"), -1);
                        builder = null;
                    } else if (currentPath != null && isManifest(ns, local, "encryption-data")) {
                        builder = new OdfEncryptionMetadata.Builder();
                        builder.checksum = attr(reader, "checksum");
                        builder.checksumType = attr(reader, "checksum-type");
                        builder.originalSize = currentSize;
                    } else if (builder != null && isManifest(ns, local, "algorithm")) {
                        builder.algorithmName = attr(reader, "algorithm-name");
                        builder.initialisationVector = attr(reader, "initialisation-vector");
                    } else if (builder != null && isManifest(ns, local, "key-derivation")) {
                        builder.keyDerivationName = attr(reader, "key-derivation-name");
                        builder.salt = attr(reader, "salt");
                        builder.iterationCount = parseInt(attr(reader, "iteration-count"), 1024);
                        builder.derivedKeySize = parseInt(attr(reader, "key-size"), 16);
                        builder.argon2Iterations = parseInt(reader.getAttributeValue(LOEXT_NS, "argon2-iterations"), 3);
                        builder.argon2Memory = parseInt(reader.getAttributeValue(LOEXT_NS, "argon2-memory"), 65536);
                        builder.argon2Lanes = parseInt(reader.getAttributeValue(LOEXT_NS, "argon2-lanes"), 4);
                    } else if (builder != null && isManifest(ns, local, "start-key-generation")) {
                        builder.startKeyGenerationName = attr(reader, "start-key-generation-name");
                        builder.startKeySize = parseInt(attr(reader, "key-size"), 20);
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String local = reader.getLocalName();
                    String ns = reader.getNamespaceURI();
                    if (isManifest(ns, local, "file-entry")) {
                        if (currentPath != null && builder != null) {
                            parser.encryptedEntries.put(currentPath, builder.build());
                        }
                        currentPath = null;
                        builder = null;
                        currentSize = -1;
                    }
                }
            }
            reader.close();
        } catch (XMLStreamException e) {
            throw new NotAnOdsException(e);
        }
        return parser;
    }

    boolean isEncrypted(String path) {
        return encryptedEntries.containsKey(path);
    }

    boolean hasEncryptedEntries() {
        return !encryptedEntries.isEmpty();
    }

    OdfEncryptionMetadata get(String path) {
        return encryptedEntries.get(path);
    }

    boolean hasEncryptedPackage() {
        return encryptedEntries.containsKey("encrypted-package");
    }

    OdfEncryptionMetadata getEncryptedPackageMetadata() {
        return encryptedEntries.get("encrypted-package");
    }

    private static boolean isManifest(String ns, String local, String name) {
        return MANIFEST_NS.equals(ns) && name.equals(local);
    }

    private static String attr(XMLStreamReader reader, String localName) {
        return reader.getAttributeValue(MANIFEST_NS, localName);
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
