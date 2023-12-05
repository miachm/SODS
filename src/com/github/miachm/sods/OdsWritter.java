package com.github.miachm.sods;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

/**
 * Internal class for generate ODS files.
 */
class OdsWritter {

    private SpreadSheet spread;
    private Compressor out;
    private Map<Style, String> stylesUsed = new HashMap<>();
    private Map<Double, String> columnStyleStringMap = new HashMap<>();
    private Map<Double, String> rowStyleStringMap = new HashMap<>();
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
            writeExtraFiles();
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

            out.writeStartDocument("UTF-8", "1.0");
            out.setPrefix("manifest", MANIFEST);
            out.writeStartElement(MANIFEST, "manifest");
            out.writeNamespace("manifest", MANIFEST);
            out.writeAttribute(MANIFEST, "version", "1.2");

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "/");
            out.writeAttribute(MANIFEST, "version", "1.2");
            out.writeAttribute(MANIFEST, "media-type", MIMETYPE);
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "content.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "styles.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();
            
            for (FileEntry entry : spread.getExtraFiles()) {
                out.writeStartElement(MANIFEST, "file-entry");
                out.writeAttribute(MANIFEST, "full-path", entry.path);
                out.writeAttribute(MANIFEST, "media-type", entry.mimetype);
                out.writeEndElement();
            }

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
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-content");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("table", TABLE);
        out.writeNamespace("text", TEXT);
        out.writeNamespace("fo", FONT);
        out.writeNamespace("style", STYLE);
        out.writeNamespace("dc", METADATA);
        out.writeNamespace("number", DATATYPE);

        out.writeAttribute(OFFICE, "version", "1.2");

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
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-styles");
        out.writeNamespace("office", OFFICE);
        out.writeAttribute(OFFICE, "version", "1.2");
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
        out.writeStartElement(OFFICE, "body");
        out.writeStartElement(OFFICE, "spreadsheet");

        for (Sheet sheet : spread.getSheets()) {
            out.writeStartElement(TABLE, "table");
            out.writeAttribute(TABLE, "name", sheet.getName());
            if (sheet.isHidden()) {
                TableStyle tableStyle = new TableStyle();
                tableStyle.setHidden(true);
                String name = tableStyleStringMap.get(tableStyle);
                if (name != null)
                    out.writeAttribute(TABLE, "style-name", name);
            }
            if (sheet.isProtected()) {
                out.writeAttribute(TABLE, "protected", "true");
                out.writeAttribute(TABLE, "protection-key", sheet.getHashedPassword());
                out.writeAttribute(TABLE, "protection-key-digest-algorithm", sheet.getHashedAlgorithm());
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
            out.writeStartElement(TABLE, "table-column");
            if (column.num_repeated > 1)
                out.writeAttribute(TABLE, "number-columns-repeated", "" + column.num_repeated);

            Double width = column.column_style.getWidth();
            if (width != null) {
                String name = columnStyleStringMap.get(width);
                if (name != null)
                    out.writeAttribute(TABLE, "style-name", name);
            }

            if (column.column_style.isHidden())
                out.writeAttribute(TABLE, "visibility", "collapse");

            Style defaultCellStyle = column.column_style.getDefaultCellStyleDangerous();
            if (!defaultCellStyle.isDefault()) {
                String name = stylesUsed.get(defaultCellStyle);
                if (name != null)
                    out.writeAttribute(TABLE, "default-cell-style-name", name);
            }

            out.writeEndElement();
        }
    }

    private void writeContent(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Row row : sheet.rows) {
            out.writeStartElement(TABLE, "table-row");
            if (row.num_repeated > 1)
                out.writeAttribute(TABLE, "number-rows-repeated", ""+row.num_repeated);
            writeRowStyles(out, row);

            for (Cell cell :  row.cells) {
                writeCell(out, cell);
            }

            out.writeEndElement();
        }
    }

    private void writeRowStyles(XMLStreamWriter out, Row row) throws XMLStreamException {
        if (row.row_style.isHidden())
            out.writeAttribute(TABLE, "visibility", "collapse");

        writeRowHeight(out, row);
    }

    private void writeCell(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        String formula = cell.getFormula();
        Style style = cell.getStyle();

        GroupCell group = cell.getGroup();
        if (group != null) {
            if (group.getCell() != cell) {
                out.writeStartElement(TABLE, "covered-table-cell");
                out.writeEndElement();
                return;
            }
        }
        out.writeStartElement(TABLE, "table-cell");
        if (cell.num_repeated > 1)
            out.writeAttribute(TABLE, "number-columns-repeated", ""+ cell.num_repeated);
        if (group != null) {
            if (group.getLength().getY() > 1)
                out.writeAttribute(TABLE, "number-columns-spanned", "" + group.getLength().getY());
            if (group.getLength().getX() > 1)
                out.writeAttribute(TABLE, "number-rows-spanned", "" + group.getLength().getX());
        }

        if (formula != null)
            out.writeAttribute(TABLE, "formula", formula);

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

            out.writeAttribute(TABLE, "style-name", key);
        }
    }

    private void writeValue(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        Object v = cell.getValue();
        if (v != null) {
            OfficeValueType valueType = OfficeValueType.ofJavaType(v.getClass());
            /*
                This fixes issue #65.
                LibreOffice only writes the "string-value" attribute for formulaic cells. Writing it for non-formulaic
                cells makes LibreOffice discard newlines when opening the sheet.
             */
            //if (valueType != OfficeValueType.STRING || cell.getFormula() != null) {
                valueType.write(v, out);
            //}

            out.writeStartElement(TEXT, "p");
            String text = v.toString();

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == ' ') {
                    out.writeStartElement(TEXT, "s");
                    int cnt = 0;
                    while (i+cnt < text.length() && text.charAt(i + cnt) == ' ') {
                        cnt++;
                    }
                    if (cnt > 1)
                        out.writeAttribute(TEXT, "c", "" + cnt);
                    i += cnt - 1 ;
                    out.writeEndElement();
                }
                else if (text.charAt(i) == '\t') {
                    out.writeEmptyElement(TEXT, "tab");
                }
                else if (text.charAt(i) == '\n') {
                    out.writeEndElement();
                    out.writeStartElement(TEXT, "p");
                }
                else if (Character.isHighSurrogate(text.charAt(i)) && i + 1 < text.length() && Character.isLowSurrogate(text.charAt(i + 1))) {
                    // write surrogate pair
                    out.writeCharacters("" + text.charAt(i) + text.charAt(i + 1));
                    i++;
                }
                else
                    out.writeCharacters("" + text.charAt(i));
            }

            out.writeEndElement();
        }
        OfficeAnnotation annotation = cell.getAnnotation();
        if (annotation != null) {
            out.writeStartElement(OFFICE, "annotation");
            if (annotation.getLastModified() != null) {
                out.writeStartElement(METADATA,  "date");
                out.writeCharacters(annotation.getLastModified().toString());
                out.writeEndElement();
            }
            if (annotation.getMsg() != null) {
                out.writeStartElement(TEXT, "p");
                out.writeCharacters(annotation.getMsg());
                out.writeEndElement();
            }
            out.writeEndElement();
        }
    }

    private void writeRowHeight(XMLStreamWriter out, Row row) throws XMLStreamException {
        Double height = row.row_style.getHeight();
        if (height != null) {
            String name = rowStyleStringMap.get(height);
            if (name != null)
                out.writeAttribute(TABLE, "style-name", name);
        }
    }

    private void writeStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(OFFICE, "automatic-styles");

        writeDataFormatStyles(out);

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

                Style defaultCellStyle = column.column_style.getDefaultCellStyleDangerous();
                if (!defaultCellStyle.isDefault()) {
                    writeCellStyle(out, defaultCellStyle);
                }
            }

            if (sheet.isHidden()) {
                writeTableStyle(out, sheet);
            }
        }
        out.writeEndElement();
    }

    private void writeDataFormatStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("number:text-style");
        out.writeAttribute(STYLE, "name", "textstyle");
        out.writeEmptyElement("number:text-content");
        out.writeEndElement();

        out.writeStartElement("number:date-style");
        out.writeAttribute(STYLE, "name", "datestyle");
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
            stylesUsed.put(style, key);
            writeCellStyle(out, style, key);
        }
    }

    private void writeCellStyle(XMLStreamWriter out, Style style, String key) throws XMLStreamException {
        for (ConditionalFormat conditionalFormat : style.getConditions()) {
            writeCellStyle(out, conditionalFormat.getStyleApplied());
        }
        out.writeStartElement(STYLE, "style");
        out.writeAttribute(STYLE, "family", "table-cell");
        out.writeAttribute(STYLE, "name", key);

        String dataStyle = style.getDataStyle();
        if (Style.PLAIN_DATA_STYLE.equals(dataStyle))
            out.writeAttribute(STYLE, "data-style-name", "textstyle");
        else if (Style.ISO_DATE_DATA_STYLE.equals(dataStyle))
            out.writeAttribute(STYLE, "data-style-name", "datestyle");

        if (style.hasTableCellProperties()) {
            out.writeStartElement(STYLE, "table-cell-properties");

            if (style.getBackgroundColor() != null) {
                out.writeAttribute(FONT, "background-color", style.getBackgroundColor().toString());
            }

            if (style.isWrap()) {
                out.writeAttribute(FONT, "wrap-option", "wrap");
            }

            if (style.getVerticalTextAligment() != null) {
                out.writeAttribute(STYLE, "vertical-align", style.getVerticalTextAligment().toString().toLowerCase());
            }

            if(style.hasBorders()) {
                writeBorderStyle(out, style);
            }

            out.writeEndElement();
        }

        for (ConditionalFormat format : style.getConditions()) {
            out.writeStartElement(STYLE, "map");
            out.writeAttribute(STYLE, "condition", format.getRawCondition());
            out.writeAttribute(STYLE, "apply-style-name", getConditionalFormatName(format.getStyleApplied()));
            out.writeEndElement();
        }

        out.writeStartElement(STYLE, "text-properties");
        if (style.isItalic())
            out.writeAttribute(FONT, "font-style", "italic");

        if (style.isBold())
            out.writeAttribute(FONT, "font-weight", "bold");

        if (style.isUnderline()) {
            out.writeAttribute(STYLE, "text-underline-style", "solid");
            out.writeAttribute(STYLE, "text-underline-type", "single");
            out.writeAttribute(STYLE, "text-underline-width", "auto");
            out.writeAttribute(STYLE, "text-underline-color", "font-color");
        }

        if (style.getFontSize() != -1)
            out.writeAttribute(FONT, "font-size", "" + style.getFontSize() + "pt");

        if (style.getFontColor() != null)
            out.writeAttribute(FONT, "color", style.getFontColor().toString());

        out.writeEndElement();

        if(style.getTextAligment() != null) {
            out.writeStartElement(STYLE, "paragraph-properties");
            out.writeAttribute(FONT, "text-align", toValue(style.getTextAligment()));
            out.writeEndElement();
        }

        out.writeEndElement();
    }

    private String getConditionalFormatName(Style style) {
        String key = stylesUsed.get(style);
        return key;
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
        if (!columnStyleStringMap.containsKey(width)) {
            String key = "co" + columnStyleStringMap.size();

            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table-column");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-column-properties");
            out.writeAttribute(STYLE, "column-width", width.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            columnStyleStringMap.put(width, key);
        }
    }

    private void writeRowStyle(XMLStreamWriter out, Double height) throws XMLStreamException {
        if (!rowStyleStringMap.containsKey(height)) {
            String key = "ro" + rowStyleStringMap.size();
            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table-row");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-row-properties");
            out.writeAttribute(STYLE, "row-height", height.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            rowStyleStringMap.put(height, key);
        }
    }

    private void writeTableStyle(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        TableStyle tableStyle = new TableStyle();
        tableStyle.setHidden(sheet.isHidden());
        if (!tableStyleStringMap.containsKey(tableStyle)) {
            String key = "tb" + tableStyleStringMap.size();
            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-properties");
            out.writeAttribute(TABLE, "display", tableStyle.isHidden() ? "false" : "true");
            out.writeEndElement();
            out.writeEndElement();

            tableStyleStringMap.put(tableStyle, key);
        }
    }
    
    private void writeBorderStyle(XMLStreamWriter out, Style style) throws XMLStreamException {

		Borders borders = style.getBorders();
		if (borders.isBorder()) {
			out.writeAttribute(FONT, "border", borders.getBorderProperties());
		}

		if (borders.isBorderTop()) {
			out.writeAttribute(FONT, "border-top", borders.getBorderTopProperties());
		}

		if (borders.isBorderBottom()) {
			out.writeAttribute(FONT, "border-bottom", borders.getBorderBottomProperties());
		}

		if (borders.isBorderLeft()) {
			out.writeAttribute(FONT, "border-left", borders.getBorderLeftProperties());
		}

		if (borders.isBorderRight()) {
			out.writeAttribute(FONT, "border-right", borders.getBorderRightProperties());
		}
    }

    private void writeExtraFiles() throws IOException {
        for (FileEntry entry : spread.getExtraFiles())
            this.out.addEntry(entry.data, entry.path);
    }
}
