package com.github.miachm.sods;

class SpreadsheetParser {
    private final StylesParser stylesParser;
    private final SpreadSheet spread;
    private final OdsOptionParameters options;

    public SpreadsheetParser(StylesParser stylesParser, SpreadSheet spread, OdsOptionParameters options) {
        this.stylesParser = stylesParser;
        this.spread = spread;
        this.options = options;
    }

    public void parseContent(XmlReaderInstance bodyInstance) {
        if (bodyInstance == null) return;
        XmlReaderInstance spreadsheetInstance = bodyInstance.nextElement("office:spreadsheet");
        if (spreadsheetInstance != null) {
            int currentSheetIndex = 0;
            while (spreadsheetInstance.hasNext()) {
                XmlReaderInstance tableInstance = spreadsheetInstance.nextElement("table:table");
                if (tableInstance != null) {
                    boolean shouldLoadSheet = options.getSheetNumbers() == null || 
                                            options.getSheetNumbers().contains(currentSheetIndex);
                    
                    if (shouldLoadSheet) {
                        String name = tableInstance.getAttribValue("table:name");
                        Sheet sheet = new Sheet(name, 0, 0);
                        SheetParser sheetParser = new SheetParser(sheet, stylesParser);
                        sheetParser.parseSheet(tableInstance);
                        spread.appendSheet(sheet);
                    }
                    currentSheetIndex++;
                }
            }
        }
    }
}