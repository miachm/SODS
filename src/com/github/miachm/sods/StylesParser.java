package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class StylesParser {
    private final OdsOptionParameters options;
    private Map<String, Style> cellStyles = new HashMap<>();
    private Map<String, ColumnStyle> columnStyles = new HashMap<>();
    private Map<String, RowStyle> rowStyles = new HashMap<>();
    private Map<String, TableStyle> tableStyles = new HashMap<>();

    public StylesParser(OdsOptionParameters options) {
        this.options = options;
        cellStyles.put("Default", new Style());
    }

    public void parseStyles(XmlReaderInstance reader) {
        if (reader == null) {
            logger().fine("No styles section found.");
            return;
        }
        logger().info("Parsing styles");
        int cellCount = 0;
        int columnCount = 0;
        int rowCount = 0;
        int tableCount = 0;
        // Each entry: [ownerStyleName, applyStyleName, conditionString]
        List<String[]> deferredMaps = new ArrayList<>();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:style");
            if (instance == null) break;
            String name = instance.getAttribValue("style:name");
            String family = instance.getAttribValue("style:family");
            if (name == null || family == null) {
                logger().warning("Skipping style with missing name or family.");
                continue;
            }
            switch (family) {
                case "table-cell":
                    Style style = readCellStyleEntry(instance, deferredMaps, name);
                    cellStyles.put(name, style);
                    cellCount++;
                    break;
                case "table-column":
                    ColumnStyle columnStyle = readColumnStyleEntry(instance);
                    columnStyles.put(name, columnStyle);
                    columnCount++;
                    break;
                case "table-row":
                    RowStyle rowStyle = readRowStyleEntry(instance);
                    rowStyles.put(name, rowStyle);
                    rowCount++;
                    break;
                case "table":
                    TableStyle tableStyle = readTableStyleEntry(instance);
                    tableStyles.put(name, tableStyle);
                    tableCount++;
                    break;
                default:
                    logger().warning("Skipping unsupported style family: " + family);
                    break;
            }
        }
        resolveConditionalFormats(deferredMaps);
        logger().info("Styles loaded: cell=" + cellCount + ", column=" + columnCount
                + ", row=" + rowCount + ", table=" + tableCount);
    }

    private void resolveConditionalFormats(List<String[]> deferredMaps) {
        for (String[] entry : deferredMaps) {
            String ownerName = entry[0];
            String applyName = entry[1];
            String condition = entry[2];
            Style ownerStyle = cellStyles.get(ownerName);
            Style applyStyle = cellStyles.get(applyName);
            if (ownerStyle == null) {
                logger().warning("Cannot resolve conditional format: owner style '" + ownerName + "' not found.");
                continue;
            }
            if (applyStyle == null) {
                logger().warning("Cannot resolve conditional format: apply style '" + applyName + "' not found.");
                continue;
            }
            ownerStyle.addCondition(new ConditionalFormat(applyStyle, condition));
        }
    }

    private Style readCellStyleEntry(XmlReaderInstance reader, List<String[]> deferredMaps, String ownerName) {
        Style style = new Style();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:text-properties",
                    "style:table-cell-properties",
                    "style:paragraph-properties",
                    "style:map");
            if (instance == null) return style;

            switch (instance.getTag()) {
                case "style:text-properties":
                    String bold = instance.getAttribValue("fo:font-weight");
                    if (bold != null) style.setBold(bold.equals("bold"));

                    String italic = instance.getAttribValue("fo:font-style");
                    if (italic != null) style.setItalic(italic.equals("italic"));

                    String underline = instance.getAttribValue("style:text-underline-style");
                    if (underline != null) style.setUnderline(underline.equals("solid"));

                    String lineThrough = instance.getAttribValue("style:text-line-through-style");
                    if (lineThrough != null) style.setLineThrough(lineThrough.equals("solid"));

                    String fontcolor = instance.getAttribValue("fo:color");
                    if (fontcolor != null && !fontcolor.equals("transparent")) {
                        try {
                            style.setFontColor(new Color(fontcolor));
                        } catch (IllegalArgumentException e) {
                            logger().warning(e.getMessage());
                        }
                    }

                    String fontsize = instance.getAttribValue("fo:font-size");
                    if (fontsize != null && fontsize.endsWith("pt")) {
                        try {
                            int index = fontsize.lastIndexOf("pt");
                            int fontSize = (int) Math.round(Double.parseDouble(fontsize.substring(0, index)));
                            style.setFontSize(fontSize);
                        } catch (NumberFormatException e) {
                            logger().warning("Invalid font size: " + fontsize);
                        }
                    }

                    String fontFamily = instance.getAttribValue("fo:font-family");
                    if (fontFamily == null || fontFamily.isEmpty()) {
                        fontFamily = instance.getAttribValue("style:font-name");
                    }
                    if (fontFamily != null && !fontFamily.isEmpty()) {
                        style.setFontFamily(fontFamily);
                    }
                    break;

                case "style:table-cell-properties":
                    String backgroundColor = instance.getAttribValue("fo:background-color");
                    if (backgroundColor != null && !backgroundColor.equals("transparent")) {
                        try {
                            style.setBackgroundColor(new Color(backgroundColor));
                        } catch (IllegalArgumentException e) {
                            logger().warning(e.getMessage());
                        }
                    }

                    String verticalAlign = instance.getAttribValue("style:vertical-align");
                    if (verticalAlign != null) {
                        Style.VERTICAL_TEXT_ALIGMENT pos = null;
                        switch (verticalAlign.toLowerCase()) {
                            case "middle": pos = Style.VERTICAL_TEXT_ALIGMENT.Middle; break;
                            case "top": pos = Style.VERTICAL_TEXT_ALIGMENT.Top; break;
                            case "bottom": pos = Style.VERTICAL_TEXT_ALIGMENT.Bottom; break;
                        }
                        style.setVerticalTextAligment(pos);
                    }
                    break;

                case "style:paragraph-properties":
                    String align = instance.getAttribValue("fo:text-align");
                    if (align != null) {
                        Style.TEXT_ALIGMENT pos = null;
                        switch (align) {
                            case "center": pos = Style.TEXT_ALIGMENT.Center; break;
                            case "end": pos = Style.TEXT_ALIGMENT.Right; break;
                            case "start": pos = Style.TEXT_ALIGMENT.Left; break;
                        }
                        style.setTextAligment(pos);
                    }
                    break;

                case "style:map":
                    String key = instance.getAttribValue("style:apply-style-name");
                    String condition = instance.getAttribValue("style:condition");
                    if (key != null && condition != null) {
                        deferredMaps.add(new String[]{ownerName, key, condition});
                    }
                    break;
            }
        }
        return style;
    }

    private ColumnStyle readColumnStyleEntry(XmlReaderInstance reader) {
        ColumnStyle style = new ColumnStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-column-properties");
            if (instance == null) return style;
            String columnWidth = instance.getAttribValue("style:column-width");
            if (columnWidth != null) style.setWidth(columnWidth);
        }
        return style;
    }

    private RowStyle readRowStyleEntry(XmlReaderInstance reader) {
        RowStyle style = new RowStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-row-properties");
            if (instance == null) return style;
            String rowHeight = instance.getAttribValue("style:row-height");
            if (rowHeight != null) style.setHeight(rowHeight);
        }
        return style;
    }

    private TableStyle readTableStyleEntry(XmlReaderInstance reader) {
        TableStyle style = new TableStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-properties");
            if (instance == null) return style;
            String display = instance.getAttribValue("table:display");
            if (display != null) style.setHidden(display.equals("false"));
            String tabColorStr = instance.getAttribValue("table:tab-color");
            if (tabColorStr != null && !tabColorStr.isEmpty()) {
                try {
                    style.setTabColor(new Color(tabColorStr));
                } catch (Exception ignored) {}
            }
        }
        return style;
    }

    public Style getCellStyle(String name) {
        return cellStyles.get(name);
    }

    public ColumnStyle getColumnStyle(String name) {
        return columnStyles.get(name);
    }

    public RowStyle getRowStyle(String name) {
        return rowStyles.get(name);
    }

    public TableStyle getTableStyle(String name) {
        return tableStyles.get(name);
    }

    private Logger logger() {
        return options == null ? Logger.getLogger("ODS_Reader") : options.getLogger();
    }
}
