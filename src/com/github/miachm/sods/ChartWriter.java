package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

class ChartWriter {
    private final SpreadSheet spread;
    private final List<ChartEntry> chartEntries = new ArrayList<>();

    ChartWriter(SpreadSheet spread) {
        this.spread = spread;
        initChartEntries();
    }

    void appendManifestEntries(XMLStreamWriter out) throws XMLStreamException {
        for (ChartEntry entry : chartEntries) {
            String objectName = entry.objectName;
            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", objectName + "/meta.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", objectName + "/styles.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", objectName + "/content.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", objectName + "/");
            out.writeAttribute(MANIFEST, "media-type", "application/vnd.oasis.opendocument.chart");
            out.writeEndElement();
        }
    }

    void writeCharts(Compressor out) throws IOException, XMLStreamException {
        for (ChartEntry entry : chartEntries) {
            byte[] content = writeChartContent(entry);
            out.addEntry(content, entry.objectName + "/content.xml");

            byte[] styles = writeChartStyles();
            out.addEntry(styles, entry.objectName + "/styles.xml");

            byte[] meta = writeChartMeta();
            out.addEntry(meta, entry.objectName + "/meta.xml");
        }
    }

    void writeDrawFrames(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        List<ChartEntry> sheetCharts = getChartEntriesForSheet(sheet);
        if (sheetCharts.isEmpty()) {
            return;
        }
        out.writeStartElement(TABLE, "shapes");
        int zIndex = 0;
        for (ChartEntry entry : sheetCharts) {
            Chart chart = entry.chart;
            out.writeStartElement(DRAW, "frame");
            out.writeAttribute(DRAW, "z-index", String.valueOf(zIndex++));
            out.writeAttribute(SVG, "width", normalizeSize(chart.getWidth(), "12cm"));
            out.writeAttribute(SVG, "height", normalizeSize(chart.getHeight(), "8cm"));
            out.writeAttribute(SVG, "x", normalizeSize(chart.getX(), "0cm"));
            out.writeAttribute(SVG, "y", normalizeSize(chart.getY(), "0cm"));

            out.writeStartElement(DRAW, "object");
            String notifyRanges = buildNotifyRanges(chart);
            if (!notifyRanges.isEmpty()) {
                out.writeAttribute(DRAW, "notify-on-update-of-ranges", notifyRanges);
            }
            out.writeAttribute(XLINK, "href", "./" + entry.objectName);
            out.writeAttribute(XLINK, "type", "simple");
            out.writeAttribute(XLINK, "show", "embed");
            out.writeAttribute(XLINK, "actuate", "onLoad");
            out.writeEndElement();

            out.writeEndElement();
        }
        out.writeEndElement();
    }

    private void initChartEntries() {
        chartEntries.clear();
        int index = 1;
        for (Sheet sheet : spread.getSheets()) {
            for (Chart chart : sheet.getCharts()) {
                if (chart.getSheet() == null) {
                    chart.setSheet(sheet);
                }
                chartEntries.add(new ChartEntry(chart, sheet, "Object " + index++));
            }
        }
    }

    private List<ChartEntry> getChartEntriesForSheet(Sheet sheet) {
        List<ChartEntry> result = new ArrayList<>();
        for (ChartEntry entry : chartEntries) {
            if (entry.sheet == sheet) {
                result.add(entry);
            }
        }
        return result;
    }

    private String normalizeSize(String size, String fallback) {
        if (size == null || size.trim().isEmpty()) {
            return fallback;
        }
        String trimmed = size.trim();
        if (trimmed.endsWith("cm") || trimmed.endsWith("mm") || trimmed.endsWith("in") || trimmed.endsWith("pt")) {
            return trimmed;
        }
        return trimmed + "cm";
    }

    private String buildNotifyRanges(Chart chart) {
        StringBuilder builder = new StringBuilder();
        appendNotifyRange(builder, chart.getCategoriesRangeAddress());
        for (ChartSeries series : chart.getSeries()) {
            appendNotifyRange(builder, series.getValuesRangeAddress());
            appendNotifyRange(builder, series.getLabelRangeAddress());
        }
        return builder.toString();
    }

    private void appendNotifyRange(StringBuilder builder, String range) {
        if (range == null || range.trim().isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(range.trim());
    }

    private byte[] writeChartContent(ChartEntry entry) throws XMLStreamException, UnsupportedEncodingException {
        Chart chart = entry.chart;
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        XMLStreamWriter out = javax.xml.stream.XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));

        out.writeStartDocument("UTF-8", "1.0");
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-content");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("chart", CHART);
        out.writeNamespace("table", TABLE);
        out.writeNamespace("text", TEXT);
        out.writeNamespace("svg", SVG);
        out.writeNamespace("draw", DRAW);
        out.writeNamespace("xlink", XLINK);
        out.writeAttribute(OFFICE, "version", "1.2");

        out.writeStartElement(OFFICE, "automatic-styles");
        out.writeEndElement();

        out.writeStartElement(OFFICE, "body");
        out.writeStartElement(OFFICE, "chart");

        out.writeStartElement(CHART, "chart");
        out.writeAttribute(CHART, "class", normalizeChartClass(chart.getType()));
        out.writeAttribute(SVG, "width", normalizeSize(chart.getWidth(), "12cm"));
        out.writeAttribute(SVG, "height", normalizeSize(chart.getHeight(), "8cm"));
        out.writeAttribute(XLINK, "href", "..");
        out.writeAttribute(XLINK, "type", "simple");

        if (chart.getTitle() != null) {
            writeChartTextElement(out, "title", chart.getTitle());
        }
        if (chart.getLegend() != null) {
            writeChartTextElement(out, "legend", chart.getLegend());
        }
        if (chart.getFooter() != null) {
            writeChartTextElement(out, "footer", chart.getFooter());
        }

        out.writeStartElement(CHART, "plot-area");
        out.writeAttribute(SVG, "x", "0cm");
        out.writeAttribute(SVG, "y", "0cm");
        out.writeAttribute(SVG, "width", normalizeSize(chart.getWidth(), "12cm"));
        out.writeAttribute(SVG, "height", normalizeSize(chart.getHeight(), "8cm"));

        out.writeStartElement(CHART, "coordinate-region");
        out.writeAttribute(SVG, "x", "0cm");
        out.writeAttribute(SVG, "y", "0cm");
        out.writeAttribute(SVG, "width", normalizeSize(chart.getWidth(), "12cm"));
        out.writeAttribute(SVG, "height", normalizeSize(chart.getHeight(), "8cm"));
        out.writeEndElement();

        out.writeStartElement(CHART, "axis");
        out.writeAttribute(CHART, "dimension", "x");
        out.writeAttribute(CHART, "name", "primary-x");
        if (chart.getXAxisLabel() != null) {
            writeAxisTitle(out, chart.getXAxisLabel());
        }
        if (chart.getCategoriesRangeAddress() != null) {
            out.writeStartElement(CHART, "categories");
            out.writeAttribute(TABLE, "cell-range-address", chart.getCategoriesRangeAddress());
            out.writeEndElement();
        }
        out.writeEndElement();

        out.writeStartElement(CHART, "axis");
        out.writeAttribute(CHART, "dimension", "y");
        out.writeAttribute(CHART, "name", "primary-y");
        if (chart.getYAxisLabel() != null) {
            writeAxisTitle(out, chart.getYAxisLabel());
        }
        out.writeEndElement();

        for (ChartSeries series : chart.getSeries()) {
            if (series.getValuesRangeAddress() == null) {
                continue;
            }
            out.writeStartElement(CHART, "series");
            out.writeAttribute(CHART, "class", normalizeChartClass(chart.getType()));
            out.writeAttribute(CHART, "values-cell-range-address", series.getValuesRangeAddress());
            if (series.getLabelRangeAddress() != null) {
                out.writeAttribute(CHART, "label-cell-address", series.getLabelRangeAddress());
            }
            out.writeEndElement();
        }

        out.writeStartElement(CHART, "wall");
        out.writeEndElement();
        out.writeStartElement(CHART, "floor");
        out.writeEndElement();

        out.writeEndElement();

        writeLocalTable(out, chart);
        out.writeEndElement();
        out.writeEndElement();
        out.writeEndElement();
        out.writeEndDocument();
        out.close();
        return output.toByteArray();
    }

    private void writeChartTextElement(XMLStreamWriter out, String element, String value) throws XMLStreamException {
        out.writeStartElement(CHART, element);
        out.writeStartElement(TEXT, "p");
        out.writeCharacters(value);
        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeAxisTitle(XMLStreamWriter out, String value) throws XMLStreamException {
        out.writeStartElement(CHART, "title");
        out.writeStartElement(TEXT, "p");
        out.writeCharacters(value);
        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeLocalTable(XMLStreamWriter out, Chart chart) throws XMLStreamException {
        List<ChartSeries> seriesList = chart.getSeries();
        ensureSeriesValues(chart, seriesList);
        int seriesCount = seriesList.size();
        int rows = chart.getCategories().size();
        if (rows == 0) {
            rows = maxSeriesValues(seriesList);
        }

        out.writeStartElement(TABLE, "table");
        out.writeAttribute(TABLE, "name", "local-table");

        out.writeStartElement(TABLE, "table-header-columns");
        out.writeStartElement(TABLE, "table-column");
        out.writeEndElement();
        out.writeEndElement();

        out.writeStartElement(TABLE, "table-columns");
        for (int i = 0; i < seriesCount; i++) {
            out.writeStartElement(TABLE, "table-column");
            out.writeEndElement();
        }
        out.writeEndElement();

        out.writeStartElement(TABLE, "table-header-rows");
        out.writeStartElement(TABLE, "table-row");
        writeTableCell(out, null, null);
        for (int i = 0; i < seriesCount; i++) {
            writeTableCell(out, "Series " + (i + 1), null);
        }
        out.writeEndElement();
        out.writeEndElement();

        out.writeStartElement(TABLE, "table-rows");
        for (int row = 0; row < rows; row++) {
            out.writeStartElement(TABLE, "table-row");
            Object category = row < chart.getCategories().size() ? chart.getCategories().get(row) : null;
            writeTableCell(out, category, chart.getCategoriesRangeAddress());
            for (int s = 0; s < seriesCount; s++) {
                ChartSeries series = seriesList.get(s);
                Object value = row < series.getValues().size() ? series.getValues().get(row) : null;
                writeTableCell(out, value, series.getValuesRangeAddress());
            }
            out.writeEndElement();
        }
        out.writeEndElement();

        out.writeEndElement();
    }

    private int maxSeriesValues(List<ChartSeries> seriesList) {
        int max = 0;
        for (ChartSeries series : seriesList) {
            max = Math.max(max, series.getValues().size());
        }
        return max;
    }

    private void ensureSeriesValues(Chart chart, List<ChartSeries> seriesList) {
        if (chart.getCategories().isEmpty() && chart.getCategoriesRangeAddress() != null) {
            List<Object> categories = resolveRangeValues(chart.getCategoriesRangeAddress());
            for (Object value : categories) {
                chart.addCategory(value);
            }
        }
        for (ChartSeries series : seriesList) {
            if (series.getValues().isEmpty() && series.getValuesRangeAddress() != null) {
                List<Object> values = resolveRangeValues(series.getValuesRangeAddress());
                for (Object value : values) {
                    series.addValue(value);
                }
            }
            if (series.getLabels().isEmpty() && series.getLabelRangeAddress() != null) {
                List<Object> labels = resolveRangeValues(series.getLabelRangeAddress());
                for (Object value : labels) {
                    series.addLabel(value);
                }
            }
        }
    }

    private List<Object> resolveRangeValues(String rangeAddress) {
        List<Object> values = new ArrayList<>();
        if (rangeAddress == null) {
            return values;
        }
        ParsedRange parsed = parseRangeAddress(rangeAddress);
        if (parsed == null) {
            return values;
        }
        Sheet sheet = spread.getSheet(parsed.sheetName);
        if (sheet == null) {
            return values;
        }
        try {
            Range range = sheet.getRange(parsed.a1Notation);
            Object[][] data = range.getValues();
            for (int row = 0; row < data.length; row++) {
                for (int column = 0; column < data[row].length; column++) {
                    values.add(data[row][column]);
                }
            }
        } catch (RuntimeException ignored) {
        }
        return values;
    }

    private ParsedRange parseRangeAddress(String rangeAddress) {
        String trimmed = rangeAddress.trim();
        if (trimmed.isEmpty()) return null;
        String[] spaceParts = trimmed.split("\\s+");
        String address = spaceParts[0];

        String[] rangeParts = address.split(":");
        String start = rangeParts[0];
        String end = rangeParts.length > 1 ? rangeParts[1] : rangeParts[0];

        SheetPart startPart = splitSheetPart(start);
        SheetPart endPart = splitSheetPart(end);
        String sheetName = startPart.sheetName != null ? startPart.sheetName : endPart.sheetName;
        if (sheetName == null) return null;
        sheetName = unquoteSheetName(sheetName);

        String a1Start = sanitizeA1(startPart.cellRef);
        String a1End = sanitizeA1(endPart.cellRef);
        String a1Notation = rangeParts.length > 1 ? a1Start + ":" + a1End : a1Start;
        if (a1Start.isEmpty()) return null;
        return new ParsedRange(sheetName, a1Notation);
    }

    private SheetPart splitSheetPart(String part) {
        if (part == null) return new SheetPart(null, "");
        int dotIndex = part.lastIndexOf('.');
        if (dotIndex < 0) {
            return new SheetPart(null, part);
        }
        String sheetName = part.substring(0, dotIndex);
        String cellRef = part.substring(dotIndex + 1);
        return new SheetPart(sheetName, cellRef);
    }

    private String sanitizeA1(String cellRef) {
        if (cellRef == null) return "";
        return cellRef.replace("$", "");
    }

    private String unquoteSheetName(String sheetName) {
        if (sheetName == null) return null;
        String trimmed = sheetName.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("'") && trimmed.endsWith("'")) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            return inner.replace("''", "'");
        }
        return trimmed;
    }

    private static class ParsedRange {
        final String sheetName;
        final String a1Notation;

        ParsedRange(String sheetName, String a1Notation) {
            this.sheetName = sheetName;
            this.a1Notation = a1Notation;
        }
    }

    private static class SheetPart {
        final String sheetName;
        final String cellRef;

        SheetPart(String sheetName, String cellRef) {
            this.sheetName = sheetName;
            this.cellRef = cellRef;
        }
    }

    private void writeTableCell(XMLStreamWriter out, Object value, String rangeDesc) throws XMLStreamException {
        out.writeStartElement(TABLE, "table-cell");
        if (value == null) {
            out.writeStartElement(TEXT, "p");
            out.writeEndElement();
            out.writeEndElement();
            return;
        }
        if (value instanceof Number) {
            out.writeAttribute(OFFICE, "value-type", "float");
            out.writeAttribute(OFFICE, "value", value.toString());
        } else {
            out.writeAttribute(OFFICE, "value-type", "string");
        }
        out.writeStartElement(TEXT, "p");
        out.writeCharacters(value.toString());
        out.writeEndElement();
        writeRangeDesc(out, rangeDesc);
        out.writeEndElement();
    }

    private void writeRangeDesc(XMLStreamWriter out, String range) throws XMLStreamException {
        if (range == null || range.trim().isEmpty()) {
            return;
        }
        out.writeStartElement(DRAW, "g");
        out.writeStartElement(SVG, "desc");
        out.writeCharacters(range.trim());
        out.writeEndElement();
        out.writeEndElement();
    }

    private byte[] writeChartStyles() throws XMLStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(512);
        XMLStreamWriter out = javax.xml.stream.XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));
        out.writeStartDocument("UTF-8", "1.0");
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-styles");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("style", STYLE);
        out.writeAttribute(OFFICE, "version", "1.2");
        out.writeStartElement(OFFICE, "styles");
        out.writeEndElement();
        out.writeEndElement();
        out.writeEndDocument();
        out.close();
        return output.toByteArray();
    }

    private byte[] writeChartMeta() throws XMLStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(512);
        XMLStreamWriter out = javax.xml.stream.XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));
        out.writeStartDocument("UTF-8", "1.0");
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-meta");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("meta", "urn:oasis:names:tc:opendocument:xmlns:meta:1.0");
        out.writeAttribute(OFFICE, "version", "1.2");
        out.writeStartElement(OFFICE, "meta");
        out.writeEndElement();
        out.writeEndElement();
        out.writeEndDocument();
        out.close();
        return output.toByteArray();
    }

    private String normalizeChartClass(String type) {
        if (type == null || type.isEmpty() || "bar".equals(type)) {
            return "chart:bar";
        }
        return type.startsWith("chart:") ? type : "chart:" + type;
    }

    private static class ChartEntry {
        final Chart chart;
        final Sheet sheet;
        final String objectName;

        ChartEntry(Chart chart, Sheet sheet, String objectName) {
            this.chart = chart;
            this.sheet = sheet;
            this.objectName = objectName;
        }
    }
}
