package com.github.miachm.sods;

class SpreadsheetParser {
    private final StylesParser stylesParser;
    private final SpreadSheet spread;
    private final OdsOptionParameters options;
    private final ChartObjectRegistry chartObjectRegistry;
    private final ImageObjectRegistry imageObjectRegistry;

    public SpreadsheetParser(StylesParser stylesParser, SpreadSheet spread, OdsOptionParameters options,
                              ChartObjectRegistry chartObjectRegistry,
                              ImageObjectRegistry imageObjectRegistry) {
        this.stylesParser = stylesParser;
        this.spread = spread;
        this.options = options;
        this.chartObjectRegistry = chartObjectRegistry;
        this.imageObjectRegistry = imageObjectRegistry;
    }

    public void parseContent(XmlReaderInstance bodyInstance) {
        options.getLogger().info("Parsing spreadsheet content");
        options.getLogger().fine("Sheet filter: " + options.getSheetNumbers());
        int currentSheetIndex = 0;
        while (bodyInstance.hasNext()) {
            XmlReaderInstance tableInstance = bodyInstance.nextElement("table:table");
            if (tableInstance != null) {
                String name = tableInstance.getAttribValue("table:name");
                boolean shouldLoadSheet = options.getSheetNumbers() == null ||
                                        options.getSheetNumbers().contains(currentSheetIndex);

                if (shouldLoadSheet) {
                    options.getLogger().info("Loading sheet " + currentSheetIndex + ": '" + name + "'");
                    Sheet sheet = new Sheet(name, 0, 0);
                    SheetParser sheetParser = new SheetParser(sheet, stylesParser, spread, options,
                            chartObjectRegistry, imageObjectRegistry);
                    sheetParser.parseSheet(tableInstance);
                    spread.appendSheet(sheet);
                } else {
                    options.getLogger().warning("Skipping sheet " + currentSheetIndex + ": '" + name + "'");
                }
                currentSheetIndex++;
            }
        }
        options.getLogger().info("Parsed " + currentSheetIndex + " sheet(s)");
    }
}
