package com.github.miachm.sods;

class OdsReaderStyle extends OdsReaderExtensionAbstract {

    OdsReaderStyle(OdsReader reader)
    {
        super("style:style", reader);
    }

    @Override
    public void processTag(XmlReaderInstance instance) {
        String name = instance.getAttribValue("style:name");
        String family = instance.getAttribValue("style:family");

        if (name != null) {
            if (family.equals("table-cell")) {
                Style style = readCellStyleEntry(instance);
                reader.defineStyle(style, name);
            }
            else if (family.equals("table-column")) {
                ColumnStyle style = readColumnStyleEntry(instance);
                reader.defineStyle(style, name);
            }
            else if (family.equals("table-row")) {
                RowStyle style = readRowStyleEntry(instance);
                reader.defineStyle(style, name);
            }
            else if (family.equals("table")) {
                TableStyle style = readTableStyleEntry(instance);
                reader.defineStyle(style, name);
            }
        }
    }

    private Style readCellStyleEntry(XmlReaderInstance reader) {
        Style style = new Style();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:text-properties",
                    "style:table-cell-properties",
                    "style:paragraph-properties");

            if (instance == null)
                return style;

            System.out.println("****" + instance.getTag());

            if (instance.getTag().equals("style:text-properties")) {
                String bold = instance.getAttribValue("fo:font-weight");
                if (bold != null)
                    style.setBold(bold.equals("bold"));

                String italic = instance.getAttribValue("fo:font-style");
                if (italic != null)
                    style.setItalic(italic.equals("italic"));

                String underline = instance.getAttribValue("style:text-underline-style");
                if (underline != null)
                    style.setUnderline(underline.equals("solid"));

                String fontcolor = instance.getAttribValue("fo:color");
                if (fontcolor != null && !fontcolor.equals("transparent"))
                    try {
                        style.setFontColor(new Color(fontcolor));
                    }
                    catch (IllegalArgumentException e) { System.err.println(e.getMessage());}

                String fontsize = instance.getAttribValue("fo:font-size");
                if (fontsize != null) {
                    if (fontsize.endsWith("pt")) {
                        try {
                            int index = fontsize.lastIndexOf("pt");
                            int fontSize = (int) Math.round(Double.parseDouble(fontsize.substring(0, index)));
                            style.setFontSize(fontSize);
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println("Error, invalid font size " + fontsize);
                        }
                    }
                    else
                        throw new OperationNotSupportedException("Error, font size is not measured in PT. Skipping...");
                }
            }

            if (instance.getTag().equals("style:table-cell-properties")) {
                String backgroundColor = instance.getAttribValue("fo:background-color");
                if (backgroundColor != null && !backgroundColor.equals("transparent"))
                    try {
                        style.setBackgroundColor(new Color(backgroundColor));
                    }
                    catch (IllegalArgumentException e) { System.err.println(e.getMessage());}

                String verticalAlign = instance.getAttribValue("style:vertical-align");
                if (verticalAlign != null) {
                    Style.VERTICAL_TEXT_ALIGMENT pos = null;
                    if (verticalAlign.equalsIgnoreCase("middle")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Middle;
                    } else if (verticalAlign.equalsIgnoreCase("top")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Top;
                    } else if (verticalAlign.equalsIgnoreCase("bottom")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Bottom;
                    }
                    style.setVerticalTextAligment(pos);
                }
            }

            if(instance.getTag().equals("style:paragraph-properties")) {
                String align = instance.getAttribValue("fo:text-align");
                if(align != null) {
                    Style.TEXT_ALIGMENT pos = null;
                    if(align.equals("center")) {
                        pos = Style.TEXT_ALIGMENT.Center;
                    }
                    else if(align.equals("end")) {
                        pos = Style.TEXT_ALIGMENT.Right;
                    }
                    else if(align.equals("start")) {
                        pos = Style.TEXT_ALIGMENT.Left;
                    }
                    style.setTextAligment(pos);
                }
            }
        }
        return style;
    }

    private ColumnStyle readColumnStyleEntry(XmlReaderInstance reader) {
        ColumnStyle style = new ColumnStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-column-properties");
            if (instance == null)
                return style;

            String columnWidth = instance.getAttribValue("style:column-width");
            if (columnWidth != null)
                style.setWidth(columnWidth);
        }
        return style;
    }

    private RowStyle readRowStyleEntry(XmlReaderInstance reader) {
        RowStyle style = new RowStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-row-properties");
            if (instance == null)
                return style;

            String rowHeight = instance.getAttribValue("style:row-height");
            if (rowHeight != null)
                style.setHeight(rowHeight);
        }
        return style;
    }

    private TableStyle readTableStyleEntry(XmlReaderInstance reader) {
        TableStyle style = new TableStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-properties");
            if (instance == null)
                return style;

            String display = instance.getAttribValue("table:display");
            if (display != null)
                style.setHidden(display.equals("false"));
        }
        return style;
    }

}
