package com.github.miachm.sods;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

class OdsReaderValues extends OdsReaderExtensionAbstract {
    private Set<Pair<Vector, Vector>> groupCells = new HashSet<>();
    private int column = 0;
    private String name = "";

    OdsReaderValues(OdsReader reader)
    {
        super("table:table-cell", reader);
    }

    @Override
    public void processTag(XmlReaderInstance instance) {
        int rows = 1;
        int columns = 1;
        String rowsSpanned = instance.getAttribValue("table:number-rows-spanned");
        if (rowsSpanned != null) {
            rows = Integer.parseInt(rowsSpanned);
        }

        String columnsSpanned = instance.getAttribValue("table:number-columns-spanned");
        if (columnsSpanned != null) {
            columns = Integer.parseInt(columnsSpanned);
        }

        Sheet sheet = reader.getCurrentSheet();
        if (sheet.getName() != this.name)
            this.column = 0;
        int positionX = sheet.getMaxRows()-1;
        int positionY = this.column;
        if (rows != 1 || columns != 1) {
            Pair<Vector, Vector> pair =  new Pair<>();
            pair.first = new Vector(positionX, positionY);
            pair.second = new Vector(rows, columns);
            groupCells.add(pair);
        }
        if (positionY >= sheet.getMaxColumns()) {
            sheet.appendColumns(positionY - sheet.getMaxColumns() + 1);
        }
        Range range = sheet.getRange(positionX, positionY);

        String formula = instance.getAttribValue("table:formula");
        if (formula != null)
            range.setFormula(formula);

        OfficeValueType valueType = OfficeValueType.ofReader(instance);
        Object value = valueType.read(instance);

        String raw = instance.getAttribValue("table:number-columns-repeated");
        int number_columns_repeated = 0;
        if (raw != null) {
            number_columns_repeated = Integer.parseInt(raw);

            // Issue #12, check function trimColumns()
            if (number_columns_repeated > 1000)
                number_columns_repeated = 1000;
        }

        if (value != null)
            range.setValue(value);

        Style style = reader.getStyle(instance.getAttribValue("table:style-name"));

        if (style != null && !style.isDefault())
            range.setStyle(style);

        readCellText(instance, range);
        this.column += columns;
/*
        last_cell_value = range.getValue();

        OfficeValueType valueType = OfficeValueType.ofReader(instance);
        Object value = valueType.read(instance);
        reader.getCurrentRange().setValue(value);*/
    }


    private void readCellText(XmlReaderInstance cellReader, Range range) {
        // A cell can contain zero(?) or more text:p tags,
        // that each can contain zero or more text:span tags.
        // Concatenate all text in them.

        StringBuffer s = new StringBuffer();

        XmlReaderInstance textElement = null;
        boolean firstTextElement = true;
        while ((textElement = cellReader.nextElement("text:p",
                "text:h", "office:annotation")) != null) {
            if (textElement.getTag().equals("office:annotation")) {
                range.setAnnotation(getOfficeAnnotation(textElement));
                continue;
            }
            // Each text:p tag seems to represent a separate row.  Separate them with newlines.
            if (firstTextElement) {
                firstTextElement = false;
            } else {
                s.append("\n");
            }

            // Add content of any contained text:span tags
            XmlReaderInstance spanElement = textElement.nextElement("text:s",
                    XmlReaderInstance.CHARACTERS);

            while (spanElement != null) {

                if (spanElement.getTag().equals("text:s")) {
                    int num = 1;
                    String atrib = spanElement.getAttribValue("text:c");
                    if (atrib != null && !atrib.isEmpty()) {
                        try {
                            num = Integer.parseInt(atrib);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number of characters: " + atrib);
                        }
                    }
                    while (num > 0) {
                        s.append(" ");
                        num--;
                    }
                }

                String spanContent = spanElement.getContent();
                if (spanContent != null) s.append(spanContent);

                spanElement = textElement.nextElement("text:s",
                        XmlReaderInstance.CHARACTERS);
            }

            // Add direct content of text:p tag (we do it here, as
            // textElement.nextElement() will not work after textElement.getContent()).

        }

        // Empty cells are supposed to be represented by null, so return that if we got no content.
        if (s.length() > 0 && (range.getValue() == null || range.getValue() instanceof String)) {
            range.setValue(s.toString());
        }
    }

    private OfficeAnnotation getOfficeAnnotation(XmlReaderInstance textElement) {
        OfficeAnnotationBuilder annotation = new OfficeAnnotationBuilder();
        StringBuilder msg = new StringBuilder();

        while (textElement.hasNext()) {
            XmlReaderInstance instance = textElement.nextElement("dc:date", "text:p");
            if (instance == null) {
                annotation.setMsg(msg.toString());
                return annotation.build();
            }

            if (instance.getTag().equals("dc:date")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (instance != null) {
                    String content = instance.getContent();
                    try {
                        if (content != null)
                            annotation.setLastModified(LocalDateTime.parse(content));
                    } catch (DateTimeParseException e) {
                        System.err.println("DATE INVALID IN OFFICE ANNOTATION");
                    }
                }
            }
            else if (instance.getTag().equals("text:p")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (msg.length() > 0)
                    msg.append("\n");

                if (instance != null) {
                    String content = instance.getContent();
                    msg.append(content);
                }
            }
        }

        annotation.setMsg(msg.toString());
        return annotation.build();
    }
}
