package com.github.miachm.sods;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * Parses ODS Basic macro module XML ({@code Basic/Standard/&lt;Name&gt;.xml}).
 */
class MacroParser {

    static final String LIBRARY_NAME = "Standard";
    private static final String BASIC_MARKER = "REM ***** BASIC *****";

    private MacroParser() {
    }

    /**
     * Returns {@code true} for module files under {@code Basic/Standard/},
     * excluding library index files.
     */
    static boolean isModuleEntry(String entryName) {
        if (entryName == null) return false;
        String prefix = "Basic/" + LIBRARY_NAME + "/";
        if (!entryName.startsWith(prefix)) return false;
        String rest = entryName.substring(prefix.length());
        if (!rest.endsWith(".xml")) return false;
        // script-lc.xml = container index (top level only); script-lb.xml = library index
        return !rest.equals("script-lb.xml") && !rest.equals("script-lc.xml");
    }

    /**
     * Reads a macro module document from {@code in}.
     *
     * @return parsed {@link Macro}, or {@code null} if the document has no module element
     */
    static Macro parseModule(InputStream in) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        XMLStreamReader reader = factory.createXMLStreamReader(in);
        try {
            String name = null;
            String language = "StarBasic";
            StringBuilder code = new StringBuilder();

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamReader.START_ELEMENT) {
                    if ("module".equals(reader.getLocalName())) {
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            String attrLocal = reader.getAttributeLocalName(i);
                            if ("name".equals(attrLocal)) {
                                name = reader.getAttributeValue(i);
                            } else if ("language".equals(attrLocal)) {
                                language = reader.getAttributeValue(i);
                            }
                        }
                    }
                } else if (event == XMLStreamReader.CHARACTERS
                        || event == XMLStreamReader.CDATA) {
                    code.append(reader.getText());
                }
            }

            if (name == null) {
                return null;
            }
            return new Macro(name, language, normalizeParsedSource(language, code.toString()));
        } finally {
            reader.close();
        }
    }

    /** Drops the LibreOffice StarBasic marker from parsed module source. */
    private static String normalizeParsedSource(String language, String code) {
        if (!"StarBasic".equals(language) || !code.contains(BASIC_MARKER)) {
            return code;
        }
        int marker = code.indexOf(BASIC_MARKER);
        String rest = code.substring(marker + BASIC_MARKER.length());
        if (rest.startsWith("\r\n")) {
            rest = rest.substring(2);
        } else if (rest.startsWith("\n") || rest.startsWith("\r")) {
            rest = rest.substring(1);
        }
        return rest.trim();
    }
}
