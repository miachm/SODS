package com.github.miachm.sods;

class ChartParser {    
    private final StylesParser stylesParser;
    private final SpreadSheet spread;
    private final OdsOptionParameters options;
    private final ChartObjectRegistry chartObjectRegistry;

    public ChartParser(StylesParser stylesParser, SpreadSheet spread, OdsOptionParameters options,
                       ChartObjectRegistry chartObjectRegistry) {
        this.stylesParser = stylesParser;
        this.spread = spread;
        this.options = options;
        this.chartObjectRegistry = chartObjectRegistry;
    }

    public void parseContent(XmlReaderInstance chartInstance, String entryName) {
        if (chartInstance == null) {
            options.getLogger().warning("Skipping chart content: empty chart instance.");
            return;
        }
        options.getLogger().info("Parsing chart content from entry: " + entryName);
        String objectPath = extractObjectPath(entryName);
        if (objectPath == null) {
            options.getLogger().warning("Skipping chart content with unknown object path: " + entryName);
            return;
        }
        while (chartInstance.hasNext()) {
            XmlReaderInstance instance = chartInstance.nextElement("chart:chart");
            if (instance == null) break;
            String type = instance.getAttribValue("chart:type");
            if (type == null) type = instance.getAttribValue("chart:class");
            if (type != null) {
                String normalizedType = normalizeChartType(type);
                if (normalizedType != null) {
                    Chart chart = parseChart(instance, normalizedType);
                    if (chart != null) {
                        chartObjectRegistry.addChart(chart, objectPath);
                        options.getLogger().info("Chart parsed: type=" + normalizedType);
                    }
                } else {
                    options.getLogger().warning("Unsupported chart type: " + type);
                }
            }
        }
    }

    public void resolveChartData() {
        for (Sheet sheet : spread.getSheets()) {
            for (Chart chart : sheet.getCharts()) {
                if (chart.getCategories().isEmpty() && chart.getCategoriesRangeAddress() != null) {
                    appendRangeValues(chart.getCategoriesRangeAddress(), chart::addCategory);
                }
                boolean hasChartData = !chart.getData().isEmpty();
                for (ChartSeries series : chart.getSeries()) {
                    if (series.getValues().isEmpty() && series.getValuesRangeAddress() != null) {
                        appendRangeValues(series.getValuesRangeAddress(), series::addValue);
                        if (!hasChartData) {
                            appendRangeValues(series.getValuesRangeAddress(),
                                    value -> chart.addData(value == null ? null : String.valueOf(value)));
                        }
                    }
                    if (series.getLabels().isEmpty() && series.getLabelRangeAddress() != null) {
                        appendRangeValues(series.getLabelRangeAddress(), series::addLabel);
                    }
                }
            }
        }
    }

    private Chart parseChart(XmlReaderInstance chartInstance, String type) {
        if (chartInstance == null) return null;

        Chart chart = new Chart();
        chart.setType(type);
        chart.setWidth(chartInstance.getAttribValue("svg:width"));
        chart.setHeight(chartInstance.getAttribValue("svg:height"));
        
        while (chartInstance.hasNext()) {
            XmlReaderInstance child = chartInstance.nextElement("chart:plot-area", "chart:title", "chart:footer", "chart:legend");
            if (child == null) break;
            if ("chart:title".equals(child.getTag())) {
                chart.setTitle(child.getContent());
            } else if ("chart:footer".equals(child.getTag())) {
                chart.setFooter(child.getContent());
            } else if ("chart:legend".equals(child.getTag())) {
                chart.setLegend(child.getContent());
            }
            else if ("chart:plot-area".equals(child.getTag())) {
                parsePlotArea(child, chart);
            }
        }
        return chart;
    }

    private void parsePlotArea(XmlReaderInstance plotAreaInstance, Chart chart) {
        if (plotAreaInstance == null) return;
        while (plotAreaInstance.hasNext()) {
            XmlReaderInstance child = plotAreaInstance.nextElement("chart:series", "chart:axis", "chart:wall", "chart:floor", "chart:title");
            if (child == null) break;
            if ("chart:axis".equals(child.getTag())) {
                String dimension = child.getAttribValue("chart:dimension");
                if ("x".equals(dimension)) {
                    String categoriesRange = readCategoriesRange(child);
                    if (categoriesRange != null) {
                        chart.setCategoriesRangeAddress(categoriesRange);
                    }
                }
            } else if ("chart:series".equals(child.getTag())) {
                ChartSeries series = new ChartSeries();
                String valuesRange = child.getAttribValue("chart:values-cell-range-address");
                String labelRange = child.getAttribValue("chart:label-cell-address");
                series.setValuesRangeAddress(valuesRange);
                series.setLabelRangeAddress(labelRange);
                if (valuesRange != null || labelRange != null) {
                    options.getLogger().fine("Chart series: values=" + valuesRange + ", labels=" + labelRange);
                }
                chart.addSeries(series);
            }
        }
    }

    private String readCategoriesRange(XmlReaderInstance axisInstance) {
        if (axisInstance == null) return null;
        while (axisInstance.hasNext()) {
            XmlReaderInstance categories = axisInstance.nextElement("chart:categories");
            if (categories == null) break;
            String range = categories.getAttribValue("table:cell-range-address");
            if (range != null) return range;
        }
        return null;
    }

    private String normalizeChartType(String type) {
        if (type == null) return null;
        String trimmed = type.trim();
        if (trimmed.isEmpty()) return null;
        if ("bar".equals(trimmed) || "chart:bar".equals(trimmed)) {
            return "bar";
        }
        if ("line".equals(trimmed) || "chart:line".equals(trimmed)) {
            return "line";
        }
        if ("area".equals(trimmed) || "chart:area".equals(trimmed)) {
            return "area";
        }
        if ("circle".equals(trimmed) || "chart:circle".equals(trimmed)) {
            return "circle";
        }
        if ("bubble".equals(trimmed) || "chart:bubble".equals(trimmed)) {
            return "bubble";
        }
        if ("filled-radar".equals(trimmed) || "chart:filled-radar".equals(trimmed)) {
            return "filled-radar";
        }
        if ("gantt".equals(trimmed) || "chart:gantt".equals(trimmed)) {
            return "gantt";
        }
        if ("radar".equals(trimmed) || "chart:radar".equals(trimmed)) {
            return "radar";
        }
        if ("ring".equals(trimmed) || "chart:ring".equals(trimmed)) {
            return "ring";
        }
        if ("scatter".equals(trimmed) || "chart:scatter".equals(trimmed)) {
            return "scatter";
        }
        if ("stock".equals(trimmed) || "chart:stock".equals(trimmed)) {
            return "stock";
        }
        if ("surface".equals(trimmed) || "chart:surface".equals(trimmed)) {
            return "surface";
        }
        return null;
    }

    private void appendRangeValues(String rangeAddress, ValueConsumer consumer) {
        Range range = resolveRange(rangeAddress);
        if (range == null) return;
        Object[][] values = range.getValues();
        for (int row = 0; row < values.length; row++) {
            for (int column = 0; column < values[row].length; column++) {
                consumer.accept(values[row][column]);
            }
        }
    }

    private Range resolveRange(String rangeAddress) {
        if (rangeAddress == null || rangeAddress.trim().isEmpty()) {
            options.getLogger().warning("Skipping chart range: empty address.");
            return null;
        }
        ParsedRange parsed = parseRangeAddress(rangeAddress);
        if (parsed == null) return null;
        Sheet sheet = spread.getSheet(parsed.sheetName);
        if (sheet == null) {
            options.getLogger().warning("Unknown sheet for chart range: " + parsed.sheetName);
            return null;
        }
        try {
            return sheet.getRange(parsed.a1Notation);
        } catch (RuntimeException ex) {
            options.getLogger().warning("Invalid chart range: " + rangeAddress);
            return null;
        }
    }

    private ParsedRange parseRangeAddress(String rangeAddress) {
        if (rangeAddress == null) return null;
        String trimmed = rangeAddress.trim();
        if (trimmed.isEmpty()) return null;
        String[] spaceParts = trimmed.split("\\s+");
        String address = spaceParts[0];

        String[] rangeParts = address.split(":");
        String start = rangeParts[0];
        String end = rangeParts.length > 1 ? rangeParts[1] : rangeParts[0];

        RangeAddressHelper.SheetPart startPart = RangeAddressHelper.splitSheetPart(start);
        RangeAddressHelper.SheetPart endPart = RangeAddressHelper.splitSheetPart(end);
        String sheetName = startPart.sheetName != null ? startPart.sheetName : endPart.sheetName;
        if (sheetName == null) {
            options.getLogger().warning("Missing sheet name in chart range: " + rangeAddress);
            return null;
        }
        sheetName = RangeAddressHelper.unquoteSheetName(sheetName);

        String a1Start = RangeAddressHelper.sanitizeA1(startPart.cellRef);
        String a1End = RangeAddressHelper.sanitizeA1(endPart.cellRef);
        String a1Notation = rangeParts.length > 1 ? a1Start + ":" + a1End : a1Start;
        if (a1Start.isEmpty()) return null;
        return new ParsedRange(sheetName, a1Notation);
    }

    private String extractObjectPath(String entryName) {
        if (entryName == null) return null;
        String trimmed = entryName.trim();
        if (trimmed.isEmpty()) return null;
        if (trimmed.endsWith("/content.xml")) {
            return trimmed.substring(0, trimmed.length() - "/content.xml".length());
        }
        return null;
    }

    private static class ParsedRange {
        final String sheetName;
        final String a1Notation;

        ParsedRange(String sheetName, String a1Notation) {
            this.sheetName = sheetName;
            this.a1Notation = a1Notation;
        }
    }

    private interface ValueConsumer {
        void accept(Object value);
    }
}
