package com.github.miachm.sods;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal class for generate ODS files.
 */
class OdsWritter {
    private final static String office = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    private final static String table_namespace = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
    private final static String text_namespace = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    private final static String font_namespace = "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0";
    private final static String style_namespace = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";

    private SpreadSheet spread;
    private Compressor out;
    private Map<Style, String> stylesUsed = new HashMap<>();
    private final String MIMETYPE= "application/vnd.oasis.opendocument.spreadsheet";

    private OdsWritter(OutputStream o, SpreadSheet spread) {
        this.spread = spread;
        this.out = new Compressor(o);
    }

    public static void save(OutputStream out,SpreadSheet spread) throws IOException {
        new OdsWritter(out,spread).save();
    }

    private void save() throws IOException {
        writeManifest();
        writeMymeType();
        try {
            writeSpreadsheet();
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        out.close();
    }

    private void writeManifest() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(output, "utf-8"));

            //final String namespace = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

            out.writeStartDocument("UTF-8", "1.0");
            out.writeStartElement("manifest:manifest");
            out.writeAttribute("manifest:version", "1.2");

            out.writeStartElement("manifest:file-entry");
            out.writeAttribute("manifest:full-path", "/");
            out.writeAttribute("manifest:version", "1.2");
            out.writeAttribute("manifest:media-type", MIMETYPE);
            out.writeEndElement();

            out.writeStartElement("manifest:file-entry");
            out.writeAttribute("manifest:full-path", "content.xml");
            out.writeAttribute("manifest:media-type", "text/xml");
            out.writeEndElement();


            out.writeEndElement();
            out.writeEndDocument();
            out.close();

            byte[] bytes = output.toByteArray();
            this.out.addEntry(bytes, "META-INF/manifest.xml");

        } catch (XMLStreamException | IOException pce) {
            throw new GenerateOdsException(pce);
        }
    }

    private void writeMymeType() throws IOException {
        out.addEntry(MIMETYPE.getBytes(),"mimetype");
    }

    private void writeSpreadsheet() throws UnsupportedEncodingException, XMLStreamException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));

        out.writeStartDocument("UTF-8", "1.0");
        out.writeStartElement( "office:document-content");
        out.writeAttribute("xmlns:office", office);
        out.writeAttribute("xmlns:table", table_namespace);
        out.writeAttribute("xmlns:text",text_namespace);
        out.writeAttribute("xmlns:fo",font_namespace);
        out.writeAttribute("xmlns:style", style_namespace);

        out.writeAttribute("office:version", "1.2");

        writeStyles(out);
        writeContent(out);

        out.writeEndElement();
        out.writeEndDocument();
        out.close();

        try {
            this.out.addEntry(output.toByteArray(),"content.xml");
        } catch (IOException e) {
            throw new GenerateOdsException(e);
        }
    }

    private void writeContent(XMLStreamWriter out/*Document dom*/) throws XMLStreamException {
        out.writeStartElement("office:body");
        out.writeStartElement("office:spreadsheet");

        for (Sheet sheet : spread.getSheets()) {
            out.writeStartElement("table:table");
            out.writeAttribute("table:name", sheet.getName());
            for (int i = 0;i < sheet.getMaxColumns();i++){
                out.writeStartElement("table:table-column");
                out.writeEndElement();
            }
            for (int i = 0;i < sheet.getMaxRows();i++) {

                out.writeStartElement("table:table-row");

                Range r = sheet.getRange(i,0,1,sheet.getMaxColumns());

                for (int j = 0; j < sheet.getMaxColumns();j++) {
                    Range range = r.getCell(0,j);
                    Object v = range.getValue();
                    String formula = range.getFormula();
                    Style style = range.getStyle();

                    out.writeStartElement("table:table-cell");

                    if (formula != null)
                        out.writeAttribute("table:formula", formula);

                    if (!style.isDefault()) {
                        String key = stylesUsed.get(style);
                        if (key == null) {
                            key = "cel" + stylesUsed.size();
                            stylesUsed.put(style, key);
                        }

                        out.writeAttribute("table:style-name", key);
                    }

                    if (v != null) {
                        String valueType = getValueType(v);

                        out.writeAttribute("office:value-type", valueType);

                        if (!valueType.equals("string"))
                            out.writeAttribute("office:value", ""+ v);

                        out.writeStartElement("text:p");

                        out.writeCharacters("" + v);

                        out.writeEndElement();
                    }

                    out.writeEndElement();
                }

                out.writeEndElement();
            }

            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("office:automatic-styles");

        for (Sheet sheet : spread.getSheets()) {
            for (int i = 0; i < sheet.getMaxRows(); i++) {
                for (int j = 0; j < sheet.getMaxColumns(); j++) {
                    Range range = sheet.getRange(i,j);
                    Style style = range.getStyle();
                        if (!style.isDefault()) {
                            String key = stylesUsed.get(style);
                            if (key == null)
                            {
                                key = "cel" + stylesUsed.size();

                                out.writeStartElement("style:style");
                                out.writeAttribute("style:family", "table-cell");
                                out.writeAttribute("style:name", key);

                                if (style.getBackgroundColor() != null) {
                                    out.writeStartElement("style:table-cell-properties");
                                    out.writeAttribute("fo:background-color", style.getBackgroundColor().toString());
                                    out.writeEndElement();
                                }

                                out.writeStartElement("style:text-properties");
                                if (style.isItalic())
                                    out.writeAttribute("fo:font-style", "italic");

                                if (style.isBold())
                                    out.writeAttribute("fo:font-weight", "bold");

                                if (style.isUnderline()) {
                                    out.writeAttribute("style:text-underline-style", "solid");
                                    out.writeAttribute("style:text-underline-type", "single");
                                    out.writeAttribute("style:text-underline-width", "auto");
                                    out.writeAttribute("style:text-underline-color", "font-color");
                                }

                                if (style.getFontSize() != -1)
                                    out.writeAttribute("fo:font-size", "" + style.getFontSize() + "pt");

                                if (style.getFontColor() != null)
                                    out.writeAttribute("fo:color", style.getFontColor().toString());

                                out.writeEndElement();
                                out.writeEndElement();
                                stylesUsed.put(style, key);
                            }
                        }
                    }
                }
        }
        out.writeEndElement();
    }

    private String getValueType(Object v) {
        if (v instanceof Integer || v instanceof Float || v instanceof Double) {
            return "float";
        }
        else {
            return "string";
        }
    }
}
