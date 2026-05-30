package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.github.miachm.sods.OpenDocumentNamespaces.MANIFEST;

/**
 * Writes {@code manifest:encryption-data} elements for encrypted package entries.
 */
class ManifestWriter {

    private ManifestWriter() {
    }

    static void writeEncryptionData(XMLStreamWriter out, OdfEncryptionMetadata meta) throws XMLStreamException {
        out.writeStartElement(MANIFEST, "encryption-data");
        out.writeAttribute(MANIFEST, "checksum", meta.checksum);
        out.writeAttribute(MANIFEST, "checksum-type", meta.checksumType);

        out.writeStartElement(MANIFEST, "algorithm");
        out.writeAttribute(MANIFEST, "algorithm-name", meta.algorithmName);
        out.writeAttribute(MANIFEST, "initialisation-vector", meta.initialisationVector);
        out.writeEndElement();

        out.writeStartElement(MANIFEST, "key-derivation");
        out.writeAttribute(MANIFEST, "key-derivation-name", meta.keyDerivationName);
        out.writeAttribute(MANIFEST, "salt", meta.salt);
        out.writeAttribute(MANIFEST, "iteration-count", String.valueOf(meta.iterationCount));
        out.writeAttribute(MANIFEST, "key-size", String.valueOf(meta.derivedKeySize));
        out.writeEndElement();

        out.writeStartElement(MANIFEST, "start-key-generation");
        out.writeAttribute(MANIFEST, "start-key-generation-name", meta.startKeyGenerationName);
        out.writeAttribute(MANIFEST, "key-size", String.valueOf(meta.startKeySize));
        out.writeEndElement();

        out.writeEndElement();
    }
}
