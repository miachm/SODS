package com.github.miachm.sods;

class SpreadsheetParser {
    private final StylesParser stylesParser;
    private final SpreadSheet spread;
    private final OdsOptionParameters options;
    private final ChartObjectRegistry chartObjectRegistry;

    public SpreadsheetParser(StylesParser stylesParser, SpreadSheet spread, OdsOptionParameters options,
                              ChartObjectRegistry chartObjectRegistry) {
        this.stylesParser = stylesParser;
        this.spread = spread;
        this.options = options;
        this.chartObjectRegistry = chartObjectRegistry;
    }

    public void parseContent(XmlReaderInstance bodyInstance) {
        options.getLogger().fine("Parsing spreadsheet content");
        int currentSheetIndex = 0;
        while (bodyInstance.hasNext()) {
            XmlReaderInstance tableInstance = bodyInstance.nextElement("table:table");
            if (tableInstance != null) {
                String name = tableInstance.getAttribValue("table:name");
                boolean shouldLoadSheet = options.getSheetNumbers() == null ||
                                        options.getSheetNumbers().contains(currentSheetIndex);

                if (shouldLoadSheet) {
                    options.getLogger().fine("Loading sheet " + currentSheetIndex + ": '" + name + "'");
                    Sheet sheet = new Sheet(name, 0, 0);
                    SheetParser sheetParser = new SheetParser(sheet, stylesParser, spread, options, chartObjectRegistry);
                    sheetParser.parseSheet(tableInstance);
                    spread.appendSheet(sheet);
                } else {
                    options.getLogger().fine("Skipping sheet " + currentSheetIndex + ": '" + name + "'");
                }
                currentSheetIndex++;
            }
        }
        options.getLogger().fine("Parsed " + currentSheetIndex + " sheet(s)");
    }
}
