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
    private Map<ColumnStyle, String> columnStyleStringMap = new HashMap<>();
    private Map<RowStyle, String> rowStyleStringMap = new HashMap<>();
    private Map<TableStyle, String> tableStyleStringMap = new HashMap<>();
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

            writeColumnsStyles(out, sheet);
            writeContent(out, sheet);

            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeColumnsStyles(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (int i = 0;i < sheet.getMaxColumns();i++){
            out.writeStartElement("table:table-column");
            Double width = sheet.getColumnWidth(i);
            if (width != null) {
                ColumnStyle columnStyle = new ColumnStyle();
                columnStyle.setWidth(width);
                String name = columnStyleStringMap.get(columnStyle);
                if (name != null)
                    out.writeAttribute("table:style-name", name);
            }

            if (sheet.columnIsHidden(i))
                out.writeAttribute("table:visibility", "collapse");

            out.writeEndElement();
        }
    }

    private void writeContent(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (int i = 0;i < sheet.getMaxRows();i++) {

            out.writeStartElement("table:table-row");
            writeRowStyles(out, sheet, i);

            for (int j = 0; j < sheet.getMaxColumns();j++) {
                Range cell = sheet.getRange(i, j);
                writeCell(out, cell);
            }

            out.writeEndElement();
        }
    }

    private void writeRowStyles(XMLStreamWriter out, Sheet sheet, int i) throws XMLStreamException {
        if (sheet.rowIsHidden(i))
            out.writeAttribute("table:visibility", "collapse");

        writeRowHeight(out, sheet, i);
    }

    private void writeCell(XMLStreamWriter out, Range range) throws XMLStreamException {
        Object v = range.getValue();
        String formula = range.getFormula();
        Style style = range.getStyle();

        Range mergedCells[] = range.getMergedCells();
        if (mergedCells.length > 0) {
            if (mergedCells[0].getColumn() != range.getColumn() || mergedCells[0].getRow() != range.getRow()) {
                out.writeStartElement("table:covered-table-cell");
                out.writeEndElement();
                return;
            }
        }
        out.writeStartElement("table:table-cell");
        if (mergedCells.length > 0) {
            if (mergedCells[0].getNumColumns() > 1)
                out.writeAttribute("table:number-columns-spanned", "" + mergedCells[0].getNumColumns());
            if (mergedCells[0].getNumRows() > 1)
                out.writeAttribute("table:number-rows-spanned", "" + mergedCells[0].getNumRows());
        }

        if (formula != null)
            out.writeAttribute("table:formula", formula);

        setCellStyle(out, style);
        writeValue(out, v);
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

    private void writeValue(XMLStreamWriter out, Object v) throws XMLStreamException {
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
    }

    private void writeRowHeight(XMLStreamWriter out, Sheet sheet, int i) throws XMLStreamException {
        Double height = sheet.getRowHeight(i);
        if (height != null) {
            RowStyle rowStyle = new RowStyle();
            rowStyle.setHeight(height);
            String name = rowStyleStringMap.get(rowStyle);
            if (name != null)
                out.writeAttribute("table:style-name", name);
        }
    }

    private void writeStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("office:automatic-styles");

        for (Sheet sheet : spread.getSheets()) {
            for (int i = 0; i < sheet.getMaxRows(); i++) {
                for (int j = 0; j < sheet.getMaxColumns(); j++) {
                    Range range = sheet.getRange(i,j);
                    Style style = range.getStyle();
                    if (!style.isDefault()) {
                        writeCellStyle(out, style);
                    }
                    Double width = sheet.getColumnWidth(j);
                    if (width != null) {
                        writeColumnStyle(out, width);
                    }
                }

                Double height = sheet.getRowHeight(i);
                if (height != null) {
                    writeRowStyle(out, height);
                }
            }

            if (sheet.isHidden()) {
                writeTableStyle(out, sheet);
            }
        }

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

			if (style.hasTableCellProperties()) {
				out.writeStartElement("style:table-cell-properties");

				if (style.getBackgroundColor() != null) {
					out.writeAttribute("fo:background-color", style.getBackgroundColor().toString());
				}

				if (style.isWrap()) {
					out.writeAttribute("fo:wrap-option", "wrap");
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
            out.writeEndElement();
            stylesUsed.put(style, key);
        }
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
