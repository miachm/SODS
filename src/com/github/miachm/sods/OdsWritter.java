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
    private final static String metadata_namespace = "http://purl.org/dc/elements/1.1/";
    private final static String datatype_namespace ="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0";

    private SpreadSheet spread;
    private Compressor out;
    private Map<Style, String> stylesUsed = new HashMap<>();
    private Map<ColumnStyle, String> columnStyleStringMap = new HashMap<>();
    private Map<RowStyle, String> rowStyleStringMap = new HashMap<>();
    private Map<TableStyle, String> tableStyleStringMap = new HashMap<>();
    private final String MIMETYPE= "application/vnd.oasis.opendocument.spreadsheet";

    private OdsWritter(OutputStream o, SpreadSheet spread) {
        this.spread = spread;
        this.out = new Compressor(o);
        spread.trimSheets();
    }

    public static void save(OutputStream out,SpreadSheet spread) throws IOException {
        new OdsWritter(out,spread).save();
    }

    private void save() throws IOException {
        writeManifest();
        writeMymeType();
        try {
            writeSpreadsheet();
            writeSettingsStyleFile();
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

            final String namespace = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

            out.writeStartDocument("UTF-8", "1.0");
            out.writeStartElement("manifest:manifest");
            out.writeAttribute("xmlns:manifest", namespace);
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
        out.writeAttribute("xmlns:dc", metadata_namespace);
        out.writeAttribute("xmlns:number", datatype_namespace);

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

    private void writeSettingsStyleFile() throws UnsupportedEncodingException, XMLStreamException {
        /*
            This is needed by the issue #45
            Excel expects a styles.xml file. Even if it's empty
         */
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));
        out.writeStartDocument("UTF-8", "1.0");
        out.writeStartElement( "office:document-styles");
        out.writeAttribute("xmlns:office", office);
        out.writeAttribute("office:version", "1.2");
        out.writeEndElement();
        out.writeEndDocument();
        out.close();

        try {
            this.out.addEntry(output.toByteArray(), "styles.xml");
        } catch (IOException e) {
            throw new GenerateOdsException(e);
        }
    }

    private void writeContent(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("office:body");
        out.writeStartElement("office:spreadsheet");

        for (Sheet sheet : spread.getSheets()) {
            out.writeStartElement("table:table");
            out.writeAttribute("table:name", sheet.getName());
            if (sheet.isHidden()) {
                TableStyle tableStyle = new TableStyle();
                tableStyle.setHidden(true);
                String name = tableStyleStringMap.get(tableStyle);
                if (name != null)
                    out.writeAttribute("table:style-name", name);
            }
            if (sheet.isProtected()) {
                out.writeAttribute("table:protected", "true");
                out.writeAttribute("table:protection-key", sheet.getHashedPassword());
                out.writeAttribute("table:protection-key-digest-algorithm", sheet.getHashedAlgorithm());
            }

            writeColumnsStyles(out, sheet);
            writeContent(out, sheet);

            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeColumnsStyles(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Column column : sheet.columns){
            out.writeStartElement("table:table-column");
            if (column.num_repeated > 1)
                out.writeAttribute("table:number-columns-repeated", "" + column.num_repeated);

            Double width = column.column_style.getWidth();
            if (width != null) {
                String name = columnStyleStringMap.get(column.column_style);
                if (name != null)
                    out.writeAttribute("table:style-name", name);
            }

            if (column.column_style.isHidden())
                out.writeAttribute("table:visibility", "collapse");

            out.writeEndElement();
        }
    }

    private void writeContent(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Row row : sheet.rows) {
            out.writeStartElement("table:table-row");
            if (row.num_repeated > 1)
                out.writeAttribute("table:number-rows-repeated", ""+row.num_repeated);
            writeRowStyles(out, row);

            for (Cell cell :  row.cells) {
                writeCell(out, cell);
            }

            out.writeEndElement();
        }
    }

    private void writeRowStyles(XMLStreamWriter out, Row row) throws XMLStreamException {
        if (row.row_style.isHidden())
            out.writeAttribute("table:visibility", "collapse");

        writeRowHeight(out, row);
    }

    private void writeCell(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        String formula = cell.getFormula();
        Style style = cell.getStyle();

        GroupCell group = cell.getGroup();
        if (group != null) {
            if (group.getCell() != cell) {
                out.writeStartElement("table:covered-table-cell");
                out.writeEndElement();
                return;
            }
        }
        out.writeStartElement("table:table-cell");
        if (cell.num_repeated > 1)
            out.writeAttribute("table:number-columns-repeated", ""+ cell.num_repeated);
        if (group != null) {
            if (group.getLength().getY() > 1)
                out.writeAttribute("table:number-columns-spanned", "" + group.getLength().getY());
            if (group.getLength().getX() > 1)
                out.writeAttribute("table:number-rows-spanned", "" + group.getLength().getX());
        }

        if (formula != null)
            out.writeAttribute("table:formula", formula);

        setCellStyle(out, style);
        writeValue(out, cell);
        out.writeEndElement();
    }

    private void setCellStyle(XMLStreamWriter out, Style style) throws XMLStreamException {
        if (!style.isDefault()) {
            String key = stylesUsed.get(style);
            if (key == null) {
                key = "cel" + stylesUsed.size();
                stylesUsed.put(style, key);
            }

            out.writeAttribute("table:style-name", key);
        }
    }

    private void writeValue(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        Object v = cell.getValue();
        if (v != null) {
            OfficeValueType valueType = OfficeValueType.ofJavaType(v.getClass());
            valueType.write(v, out);

            out.writeStartElement("text:p");
            String text = v.toString();

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == ' ') {
                    out.writeStartElement("text:s");
                    int cnt = 0;
                    while (i+cnt < text.length() && text.charAt(i + cnt) == ' ') {
                        cnt++;
                    }
                    if (cnt > 1)
                        out.writeAttribute("text:c", "" + cnt);
                    i += cnt - 1 ;
                    out.writeEndElement();
                }
                else if (text.charAt(i) == '\t') {
                    out.writeEmptyElement("text:tab");
                }
                else if (text.charAt(i) == '\n') {
                    out.writeEndElement();
                    out.writeStartElement("text:p");
                }
                else
                    out.writeCharacters("" + text.charAt(i));
            }

            out.writeEndElement();
        }
        OfficeAnnotation annotation = cell.getAnnotation();
        if (annotation != null) {
            out.writeStartElement("office:annotation");
            if (annotation.getLastModified() != null) {
                out.writeStartElement("dc:date");
                out.writeCharacters(annotation.getLastModified().toString());
                out.writeEndElement();
            }
            if (annotation.getMsg() != null) {
                out.writeStartElement("text:p");
                out.writeCharacters(annotation.getMsg());
                out.writeEndElement();
            }
            out.writeEndElement();
        }
    }

    private void writeRowHeight(XMLStreamWriter out, Row row) throws XMLStreamException {
        Double height = row.row_style.getHeight();
        if (height != null) {
            String name = rowStyleStringMap.get(row.row_style);
            if (name != null)
                out.writeAttribute("table:style-name", name);
        }
    }

    private void writeStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("office:automatic-styles");

        writeDateFormatStyle(out);

        for (Sheet sheet : spread.getSheets()) {
            for (Row row : sheet.rows) {
                for (Cell cell : row.cells) {
                    Style style = cell.getStyle();

                    if (!style.isDefault()) {
                        writeCellStyle(out, style);
                    }
                }

                Double height = row.row_style.getHeight();
                if (height != null) {
                    writeRowStyle(out, height);
                }
            }

            for (Column column : sheet.columns) {
                Double width = column.column_style.getWidth();
                if (width != null) {
                    writeColumnStyle(out, width);
                }
            }

            if (sheet.isHidden()) {
                writeTableStyle(out, sheet);
            }
        }
        out.writeEndElement();
    }

    private void writeDateFormatStyle(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("number:date-style");
        out.writeAttribute("style:name", "datestyle");
        out.writeStartElement("number:year");
        out.writeAttribute("number:style", "long");
        out.writeEndElement();
        out.writeStartElement("number:text");
        out.writeCharacters("-");
        out.writeEndElement();
        out.writeStartElement("number:month");
        out.writeAttribute("number:style", "long");
        out.writeEndElement();
        out.writeStartElement("number:text");
        out.writeCharacters("-");
        out.writeEndElement();
        out.writeEmptyElement("number:day");
        out.writeEndElement();
    }

    private void writeCellStyle(XMLStreamWriter out, Style style) throws XMLStreamException {

    	String key = stylesUsed.get(style);
        if (key == null)
        {
            key = "cel" + stylesUsed.size();

            out.writeStartElement("style:style");
            out.writeAttribute("style:family", "table-cell");
            out.writeAttribute("style:name", key);

            if (style.isDate())
                out.writeAttribute("style:data-style-name", "datestyle");

            if (style.hasTableCellProperties()) {
				out.writeStartElement("style:table-cell-properties");

				if (style.getBackgroundColor() != null) {
					out.writeAttribute("fo:background-color", style.getBackgroundColor().toString());
				}

				if (style.isWrap()) {
					out.writeAttribute("fo:wrap-option", "wrap");
				}

				if (style.getVerticalTextAligment() != null) {
                    out.writeAttribute("style:vertical-align", style.getVerticalTextAligment().toString().toLowerCase());
                }
				
				if(style.hasBorders()) {
					writeBorderStyle(out, style);
				}

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

            if(style.getTextAligment() != null) {
                out.writeStartElement("style:paragraph-properties");
                out.writeAttribute("fo:text-align", toValue(style.getTextAligment()));
                out.writeEndElement();
            }

            out.writeEndElement();
            stylesUsed.put(style, key);
        }
    }

    private String toValue(Style.TEXT_ALIGMENT textAligment) {
        switch (textAligment)
        {
            case Left:
                return "start";
            case Center:
                return "center";
            case Right:
                return "end";
        }
        return null;
    }

    private void writeColumnStyle(XMLStreamWriter out, Double width) throws XMLStreamException {
        ColumnStyle columnStyle = new ColumnStyle();
        columnStyle.setWidth(width);
        if (!columnStyleStringMap.containsKey(columnStyle)) {
            String key = "co" + columnStyleStringMap.size();

            out.writeStartElement("style:style");
            out.writeAttribute("style:family", "table-column");
            out.writeAttribute("style:name", key);
            out.writeStartElement("style:table-column-properties");
            out.writeAttribute("style:column-width", width.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            columnStyleStringMap.put(columnStyle, key);
        }
    }

    private void writeRowStyle(XMLStreamWriter out, Double height) throws XMLStreamException {
        RowStyle rowStyle = new RowStyle();
        rowStyle.setHeight(height);
        if (!rowStyleStringMap.containsKey(rowStyle)) {
            String key = "ro" + rowStyleStringMap.size();
            out.writeStartElement("style:style");
            out.writeAttribute("style:family", "table-row");
            out.writeAttribute("style:name", key);
            out.writeStartElement("style:table-row-properties");
            out.writeAttribute("style:row-height", height.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            rowStyleStringMap.put(rowStyle, key);
        }
    }

    private void writeTableStyle(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        TableStyle tableStyle = new TableStyle();
        tableStyle.setHidden(sheet.isHidden());
        if (!tableStyleStringMap.containsKey(tableStyle)) {
            String key = "tb" + tableStyleStringMap.size();
            out.writeStartElement("style:style");
            out.writeAttribute("style:family", "table");
            out.writeAttribute("style:name", key);
            out.writeStartElement("style:table-properties");
            out.writeAttribute("table:display", tableStyle.isHidden() ? "false" : "true");
            out.writeEndElement();
            out.writeEndElement();

            tableStyleStringMap.put(tableStyle, key);
        }
    }
    
    private void writeBorderStyle(XMLStreamWriter out, Style style) throws XMLStreamException {
    	
		Borders borders = style.getBorders();
		if (borders.isBorder()) {
			out.writeAttribute("fo:border", borders.getBorderProperties());
		}

		if (borders.isBorderTop()) {
			out.writeAttribute("fo:border-top", borders.getBorderTopProperties());
		}

		if (borders.isBorderBottom()) {
			out.writeAttribute("fo:border-bottom", borders.getBorderBottomProperties());
		}

		if (borders.isBorderLeft()) {
			out.writeAttribute("fo:border-left", borders.getBorderLeftProperties());
		}

		if (borders.isBorderRight()) {
			out.writeAttribute("fo:border-right", borders.getBorderRightProperties());
		}
    }
}
