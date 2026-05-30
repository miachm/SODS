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

    interface ManifestFileEntryWriter {
        void write(XMLStreamWriter out, String path, String mediaType) throws XMLStreamException;
    }

    interface PackageEntryWriter {
        void write(byte[] data, String path) throws IOException;
    }

    void appendManifestEntries(XMLStreamWriter out, ManifestFileEntryWriter writer) throws XMLStreamException {
        for (ChartEntry entry : chartEntries) {
            String objectName = entry.objectName;
            writer.write(out, objectName + "/meta.xml", "text/xml");
            writer.write(out, objectName + "/styles.xml", "text/xml");
            writer.write(out, objectName + "/content.xml", "text/xml");
            writer.write(out, objectName + "/", "application/vnd.oasis.opendocument.chart");
        }
    }

    void writeCharts(PackageEntryWriter writer) throws IOException, XMLStreamException {
        for (ChartEntry entry : chartEntries) {
            byte[] content = writeChartContent(entry);
            writer.write(content, entry.objectName + "/content.xml");

            byte[] styles = writeChartStyles();
            writer.write(styles, entry.objectName + "/styles.xml");

            byte[] meta = writeChartMeta();
            writer.write(meta, entry.objectName + "/meta.xml");
        }
    }

    void writeDrawFrames(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        List<ChartEntry> sheetCharts = getChartEntriesForSheet(sheet);
        if (sheetCharts.isEmpty()) {
            return;
        }
        out.writeStartElement(TABLE, "shapes");
        writeDrawFramesContent(out, sheet, 0);
        out.writeEndElement();
    }

    int writeDrawFramesContent(XMLStreamWriter out, Sheet sheet, int startZIndex) throws XMLStreamException {
        List<ChartEntry> sheetCharts = getChartEntriesForSheet(sheet);
        int zIndex = startZIndex;
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
        return zIndex;
    }

    boolean hasCharts(Sheet sheet) {
        return !getChartEntriesForSheet(sheet).isEmpty();
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
        out.writeNamespace("chartooo", CHART_OOO);
        out.writeNamespace("table", TABLE);
        out.writeNamespace("text", TEXT);
        out.writeNamespace("svg", SVG);
        out.writeNamespace("draw", DRAW);
        out.writeNamespace("style", STYLE);
        out.writeNamespace("fo", FONT);
        out.writeNamespace("number", DATATYPE);
        out.writeNamespace("dr3d", DR3D);
        out.writeNamespace("loext", LOEXT);
        out.writeNamespace("xlink", XLINK);
        out.writeAttribute(OFFICE, "version", "1.2");

        out.writeStartElement(OFFICE, "automatic-styles");
        writeChartStyles(out, chart);
        out.writeEndElement();

        out.writeStartElement(OFFICE, "body");
        out.writeStartElement(OFFICE, "chart");

        out.writeStartElement(CHART, "chart");
        out.writeAttribute(CHART, "class", normalizeChartClass(chart.getType()));
        out.writeAttribute(CHART, "style-name", "ch1");
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
        out.writeAttribute(CHART, "style-name", isLineChart(chart) ? "ch3" : "ch2");
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
        out.writeAttribute(CHART, "style-name", getXAxisStyleName(chart));
        out.writeAttribute(CHART_OOO, "axis-type", "auto");
        out.writeStartElement(CHART_OOO, "date-scale");
        out.writeEndElement();
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
        out.writeAttribute(CHART, "style-name", getYAxisStyleName(chart));
        if (chart.getYAxisLabel() != null) {
            writeAxisTitle(out, chart.getYAxisLabel());
        }
        out.writeStartElement(CHART, "grid");
        out.writeAttribute(CHART, "style-name", "ch5");
        out.writeAttribute(CHART, "class", "major");
        out.writeEndElement();
        out.writeEndElement();

        int seriesIndex = 0;
        for (ChartSeries series : chart.getSeries()) {
            if (series.getValuesRangeAddress() == null) {
                continue;
            }
            out.writeStartElement(CHART, "series");
            out.writeAttribute(CHART, "class", normalizeChartClass(chart.getType()));
            if (series.getStyle().hasAnyProperties()) {
                out.writeAttribute(CHART, "style-name", "ch-series-" + (seriesIndex + 1));
            } else {
                out.writeAttribute(CHART, "style-name", "ch6");
            }
            out.writeAttribute(CHART, "values-cell-range-address", series.getValuesRangeAddress());
            if (series.getLabelRangeAddress() != null) {
                out.writeAttribute(CHART, "label-cell-address", series.getLabelRangeAddress());
            }
            out.writeEndElement();
            seriesIndex++;
        }

        if (chart.isShowWall()) {
            out.writeStartElement(CHART, "wall");
            out.writeAttribute(CHART, "style-name", "ch7");
            out.writeEndElement();
        }

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
        List<Object> categories = chart.getCategoriesRangeAddress() != null
                ? resolveRangeValues(chart.getCategoriesRangeAddress())
                : chart.getCategories();
        RangeAddress categoriesAddress = parseRangeAddressWithCoords(chart.getCategoriesRangeAddress());
        RangeAddress[] seriesAddresses = new RangeAddress[seriesList.size()];
        List<List<Object>> seriesValuesList = new ArrayList<>();
        for (int i = 0; i < seriesList.size(); i++) {
            ChartSeries series = seriesList.get(i);
            seriesAddresses[i] = parseRangeAddressWithCoords(series.getValuesRangeAddress());
            List<Object> values = series.getValuesRangeAddress() != null
                    ? resolveRangeValues(series.getValuesRangeAddress())
                    : series.getValues();
            seriesValuesList.add(values);
        }
        int seriesCount = seriesList.size();
        int rows = categories.size();
        if (rows == 0) {
            rows = maxSeriesValues(seriesValuesList);
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
            Object category = row < categories.size() ? categories.get(row) : null;
            writeTableCell(out, category, buildCellDesc(categoriesAddress, row, 0));
            for (int s = 0; s < seriesCount; s++) {
                List<Object> values = seriesValuesList.get(s);
                Object value = row < values.size() ? values.get(row) : null;
                writeTableCell(out, value, buildCellDesc(seriesAddresses[s], row, 0));
            }
            out.writeEndElement();
        }
        out.writeEndElement();

        out.writeEndElement();
    }

    private int maxSeriesValues(List<List<Object>> seriesValuesList) {
        int max = 0;
        for (List<Object> values : seriesValuesList) {
            max = Math.max(max, values.size());
        }
        return max;
    }

    private List<Object> resolveRangeValues(String rangeAddress) {
        List<Object> values = new ArrayList<>();
        if (rangeAddress == null) {
            return values;
        }
        ParsedRange parsed = parseRangeAddress(rangeAddress);
        if (parsed == null) {
            throw buildRangeException(rangeAddress, null);
        }
        Sheet sheet = spread.getSheet(parsed.sheetName);
        if (sheet == null) {
            throw buildRangeException(rangeAddress, null);
        }
        try {
            Range range = sheet.getRange(parsed.a1Notation);
            Object[][] data = range.getValues();
            for (int row = 0; row < data.length; row++) {
                for (int column = 0; column < data[row].length; column++) {
                    values.add(data[row][column]);
                }
            }
        } catch (RuntimeException ex) {
            throw buildRangeException(rangeAddress, ex);
        }
        return values;
    }

    private ParsedRange parseRangeAddress(String rangeAddress) {
        if (rangeAddress == null) {
            return null;
        }
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
        if (sheetName == null) return null;
        sheetName = RangeAddressHelper.unquoteSheetName(sheetName);

        String a1Start = RangeAddressHelper.sanitizeA1(startPart.cellRef);
        String a1End = RangeAddressHelper.sanitizeA1(endPart.cellRef);
        String a1Notation = rangeParts.length > 1 ? a1Start + ":" + a1End : a1Start;
        if (a1Start.isEmpty()) return null;
        return new ParsedRange(sheetName, a1Notation);
    }

    private static class ParsedRange {
        final String sheetName;
        final String a1Notation;

        ParsedRange(String sheetName, String a1Notation) {
            this.sheetName = sheetName;
            this.a1Notation = a1Notation;
        }
    }

    private RangeAddress parseRangeAddressWithCoords(String rangeAddress) {
        if (rangeAddress == null) {
            return null;
        }
        ParsedRange parsed = parseRangeAddress(rangeAddress);
        if (parsed == null) {
            throw buildRangeException(rangeAddress, null);
        }
        try {
            A1NotationCord cord = new A1NotationCord(parsed.a1Notation);
            int initRow = cord.getInitRow();
            int initColumn = cord.getInitColumn();
            int rows = cord.getLastRow() - initRow + 1;
            int columns = cord.getLastColumn() - initColumn + 1;
            return new RangeAddress(parsed.sheetName, initRow, initColumn, rows, columns);
        } catch (RuntimeException ex) {
            throw buildRangeException(rangeAddress, ex);
        }
    }

    private IndexOutOfBoundsException buildRangeException(String rangeAddress, RuntimeException cause) {
        IndexOutOfBoundsException ex = new IndexOutOfBoundsException("Invalid chart range: " + rangeAddress);
        if (cause != null) {
            ex.initCause(cause);
        }
        return ex;
    }

    private String buildCellDesc(RangeAddress address, int rowOffset, int columnOffset) {
        if (address == null) {
            return null;
        }
        int row = address.initRow + rowOffset;
        int column = address.initColumn + columnOffset;
        if (rowOffset >= address.rows || columnOffset >= address.columns) {
            return null;
        }
        return address.sheetName + "." + toA1(column, row);
    }

    private String toA1(int column, int row) {
        return toColumnName(column) + (row + 1);
    }

    private String toColumnName(int column) {
        int col = column;
        StringBuilder sb = new StringBuilder();
        while (col >= 0) {
            int rem = col % 26;
            sb.insert(0, (char) ('A' + rem));
            col = (col / 26) - 1;
        }
        return sb.toString();
    }

    private static class RangeAddress {
        final String sheetName;
        final int initRow;
        final int initColumn;
        final int rows;
        final int columns;

        RangeAddress(String sheetName, int initRow, int initColumn, int rows, int columns) {
            this.sheetName = sheetName;
            this.initRow = initRow;
            this.initColumn = initColumn;
            this.rows = rows;
            this.columns = columns;
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

    private void writeChartStyles(XMLStreamWriter out, Chart chart) throws XMLStreamException {
        writeNumberStyle(out, "N0");

        ChartStyle chartDefaults = new ChartStyle();
        chartDefaults.getGraphicProperties().setStroke("none");
        writeStyleWithDefaults(out, "ch1", chartDefaults, chart.getStyle(), null, null);

        ChartStyle axisDefaults = new ChartStyle();
        axisDefaults.getChartProperties().setDisplayLabel(chart.isDisplayLabel());
        axisDefaults.getChartProperties().setLogarithmic(false);
        axisDefaults.getChartProperties().setReverseDirection(false);
        axisDefaults.getChartProperties().setTextLineBreak(false);
        axisDefaults.getChartProperties().setTryStaggeringFirst(false);
        axisDefaults.getChartProperties().setLinkDataStyleToSource(true);
        axisDefaults.getChartProperties().setAxisPosition(0);
        axisDefaults.getGraphicProperties().setStrokeColor(new Color("#b3b3b3"));
        axisDefaults.getTextProperties().setFontSizePt(10);
        axisDefaults.getTextProperties().setFontSizeAsianPt(10);
        axisDefaults.getTextProperties().setFontSizeComplexPt(10);

        ChartStyle gridDefaults = new ChartStyle();
        gridDefaults.getGraphicProperties().setStrokeColor(new Color("#b3b3b3"));

        ChartStyle wallDefaults = new ChartStyle();
        wallDefaults.getGraphicProperties().setStroke("solid");
        wallDefaults.getGraphicProperties().setStrokeColor(new Color("#b3b3b3"));
        wallDefaults.getGraphicProperties().setFill("none");
        wallDefaults.getGraphicProperties().setFillColor(new Color("#e6e6e6"));

        if (isLineChart(chart)) {
            ChartStyle legendDefaults = new ChartStyle();
            legendDefaults.getChartProperties().setAutoPosition(true);
            legendDefaults.getGraphicProperties().setStroke("none");
            legendDefaults.getGraphicProperties().setStrokeColor(new Color("#b3b3b3"));
            legendDefaults.getGraphicProperties().setFill("none");
            legendDefaults.getGraphicProperties().setFillColor(new Color("#e6e6e6"));
            legendDefaults.getTextProperties().setFontSizePt(10);
            legendDefaults.getTextProperties().setFontSizeAsianPt(10);
            legendDefaults.getTextProperties().setFontSizeComplexPt(10);
            writeStyleWithDefaults(out, "ch2", legendDefaults, new ChartStyle(), null, null);

            ChartStyle plotDefaults = new ChartStyle();
            plotDefaults.getChartProperties().setSymbolType("automatic");
            plotDefaults.getChartProperties().setIncludeHiddenCells(false);
            plotDefaults.getChartProperties().setTreatEmptyCells(ChartProperties.TreatEmptyCells.LeaveGap);
            plotDefaults.getChartProperties().setRightAngledAxes(true);
            writeStyleWithDefaults(out, "ch3", plotDefaults, chart.getPlotAreaStyle(), null, null);

            if (hasLineCustomAxes(chart)) {
                writeStyleWithDefaults(out, "ch4x", axisDefaults, chart.getXAxis().getStyle(), chart.isDisplayLabel(), "N0");
                writeStyleWithDefaults(out, "ch4y", axisDefaults, chart.getYAxis().getStyle(), chart.isDisplayLabel(), "N0");
            } else {
                writeStyleWithDefaults(out, "ch4", axisDefaults, chart.getXAxis().getStyle(), chart.isDisplayLabel(), "N0");
            }

            writeStyleWithDefaults(out, "ch5", gridDefaults, new ChartStyle(), null, null);

            ChartStyle seriesDefaults = buildLineSeriesDefaults();
            writeStyleWithDefaults(out, "ch6", seriesDefaults, new ChartStyle(), null, "N0");

            if (chart.isShowWall()) {
                writeStyleWithDefaults(out, "ch7", wallDefaults, chart.getWallStyle(), null, null);
            }

            int seriesIndex = 0;
            for (ChartSeries series : chart.getSeries()) {
                if (series.getValuesRangeAddress() == null) {
                    continue;
                }
                if (series.getStyle().hasAnyProperties()) {
                    writeStyleWithDefaults(out, "ch-series-" + (seriesIndex + 1), seriesDefaults, series.getStyle(), null, "N0");
                }
                seriesIndex++;
            }
        } else {
            ChartStyle plotDefaults = new ChartStyle();
            plotDefaults.getChartProperties().setIncludeHiddenCells(false);
            plotDefaults.getChartProperties().setAutoPosition(true);
            plotDefaults.getChartProperties().setAutoSize(true);
            plotDefaults.getChartProperties().setTreatEmptyCells(ChartProperties.TreatEmptyCells.LeaveGap);
            plotDefaults.getChartProperties().setRightAngledAxes(true);
            writeStyleWithDefaults(out, "ch2", plotDefaults, chart.getPlotAreaStyle(), null, null);

            writeStyleWithDefaults(out, "ch3", axisDefaults, chart.getXAxis().getStyle(), chart.isDisplayLabel(), "N0");
            writeStyleWithDefaults(out, "ch4", axisDefaults, chart.getYAxis().getStyle(), chart.isDisplayLabel(), "N0");

            writeStyleWithDefaults(out, "ch5", gridDefaults, new ChartStyle(), null, null);

            ChartStyle seriesDefaults = buildBarSeriesDefaults();
            writeStyleWithDefaults(out, "ch6", seriesDefaults, new ChartStyle(), null, "N0");

            if (chart.isShowWall()) {
                writeStyleWithDefaults(out, "ch7", wallDefaults, chart.getWallStyle(), null, null);
            }

            int seriesIndex = 0;
            for (ChartSeries series : chart.getSeries()) {
                if (series.getValuesRangeAddress() == null) {
                    continue;
                }
                if (series.getStyle().hasAnyProperties()) {
                    writeStyleWithDefaults(out, "ch-series-" + (seriesIndex + 1), seriesDefaults, series.getStyle(), null, "N0");
                }
                seriesIndex++;
            }
        }
    }

    private void writeNumberStyle(XMLStreamWriter out, String name) throws XMLStreamException {
        out.writeStartElement(DATATYPE, "number-style");
        out.writeAttribute(STYLE, "name", name);
        out.writeStartElement(DATATYPE, "number");
        out.writeAttribute(DATATYPE, "min-integer-digits", "1");
        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeStyleWithDefaults(XMLStreamWriter out,
                                        String styleName,
                                        ChartStyle defaults,
                                        ChartStyle custom,
                                        Boolean displayLabelOverride,
                                        String dataStyleName) throws XMLStreamException {
        ChartProperties chartDefaults = defaults.getChartProperties();
        ChartGraphicProperties graphicDefaults = defaults.getGraphicProperties();
        ChartTextProperties textDefaults = defaults.getTextProperties();
        ChartProperties chartCustom = custom.getChartProperties();
        ChartGraphicProperties graphicCustom = custom.getGraphicProperties();
        ChartTextProperties textCustom = custom.getTextProperties();

        out.writeStartElement(STYLE, "style");
        out.writeAttribute(STYLE, "name", styleName);
        out.writeAttribute(STYLE, "family", "chart");
        if (dataStyleName != null) {
            out.writeAttribute(STYLE, "data-style-name", dataStyleName);
        }

        if (chartDefaults.hasAny() || chartCustom.hasAny() || displayLabelOverride != null) {
            out.writeStartElement(STYLE, "chart-properties");
            writeChartProperties(out, chartDefaults, chartCustom, displayLabelOverride);
            out.writeEndElement();
        }

        if (graphicDefaults.hasAny() || graphicCustom.hasAny()) {
            out.writeStartElement(STYLE, "graphic-properties");
            writeGraphicProperties(out, graphicDefaults, graphicCustom);
            out.writeEndElement();
        }

        if (textDefaults.hasAny() || textCustom.hasAny()) {
            out.writeStartElement(STYLE, "text-properties");
            writeTextProperties(out, textDefaults, textCustom);
            out.writeEndElement();
        }

        out.writeEndElement();
    }

    private void writeChartProperties(XMLStreamWriter out,
                                      ChartProperties defaults,
                                      ChartProperties custom,
                                      Boolean displayLabelOverride) throws XMLStreamException {
        Boolean displayLabel = pick(custom.getDisplayLabel(), defaults.getDisplayLabel());
        if (displayLabel == null) {
            displayLabel = displayLabelOverride;
        }
        if (displayLabel != null) {
            out.writeAttribute(CHART, "display-label", displayLabel ? "true" : "false");
        }
        writeBooleanAttr(out, CHART, "logarithmic", pick(custom.getLogarithmic(), defaults.getLogarithmic()));
        writeBooleanAttr(out, CHART, "reverse-direction", pick(custom.getReverseDirection(), defaults.getReverseDirection()));
        writeBooleanAttr(out, CHART, "link-data-style-to-source",
                pick(custom.getLinkDataStyleToSource(), defaults.getLinkDataStyleToSource()));
        writeBooleanAttr(out, CHART, "auto-position", pick(custom.getAutoPosition(), defaults.getAutoPosition()));
        writeBooleanAttr(out, CHART, "auto-size", pick(custom.getAutoSize(), defaults.getAutoSize()));
        writeBooleanAttr(out, CHART, "right-angled-axes", pick(custom.getRightAngledAxes(), defaults.getRightAngledAxes()));
        writeBooleanAttr(out, CHART, "include-hidden-cells",
                pick(custom.getIncludeHiddenCells(), defaults.getIncludeHiddenCells()));
        String symbolType = pick(custom.getSymbolType(), defaults.getSymbolType());
        if (symbolType != null) {
            out.writeAttribute(CHART, "symbol-type", symbolType);
        }
        String symbolName = pick(custom.getSymbolName(), defaults.getSymbolName());
        if (symbolName != null) {
            out.writeAttribute(CHART, "symbol-name", symbolName);
        }
        String symbolWidth = pick(custom.getSymbolWidth(), defaults.getSymbolWidth());
        if (symbolWidth != null) {
            out.writeAttribute(CHART, "symbol-width", symbolWidth);
        }
        String symbolHeight = pick(custom.getSymbolHeight(), defaults.getSymbolHeight());
        if (symbolHeight != null) {
            out.writeAttribute(CHART, "symbol-height", symbolHeight);
        }
        writeBooleanAttr(out, TEXT, "line-break", pick(custom.getTextLineBreak(), defaults.getTextLineBreak()));
        writeBooleanAttr(out, LOEXT, "try-staggering-first",
                pick(custom.getTryStaggeringFirst(), defaults.getTryStaggeringFirst()));

        ChartProperties.TreatEmptyCells treatEmptyCells = pick(custom.getTreatEmptyCells(), defaults.getTreatEmptyCells());
        if (treatEmptyCells != null) {
            out.writeAttribute(CHART, "treat-empty-cells", treatEmptyCells.getValue());
        }
        Integer axisPosition = pick(custom.getAxisPosition(), defaults.getAxisPosition());
        if (axisPosition != null) {
            out.writeAttribute(CHART, "axis-position", String.valueOf(axisPosition));
        }
    }

    private void writeGraphicProperties(XMLStreamWriter out,
                                        ChartGraphicProperties defaults,
                                        ChartGraphicProperties custom) throws XMLStreamException {
        String stroke = pick(custom.getStroke(), defaults.getStroke());
        if (stroke != null) {
            out.writeAttribute(DRAW, "stroke", stroke);
        }
        Color strokeColor = pick(custom.getStrokeColor(), defaults.getStrokeColor());
        if (strokeColor != null) {
            out.writeAttribute(SVG, "stroke-color", strokeColor.toString());
        }
        String strokeWidth = pick(custom.getStrokeWidth(), defaults.getStrokeWidth());
        if (strokeWidth != null) {
            out.writeAttribute(SVG, "stroke-width", strokeWidth);
        }
        String fill = pick(custom.getFill(), defaults.getFill());
        if (fill != null) {
            out.writeAttribute(DRAW, "fill", fill);
        }
        Color fillColor = pick(custom.getFillColor(), defaults.getFillColor());
        if (fillColor != null) {
            out.writeAttribute(DRAW, "fill-color", fillColor.toString());
        }
        String edgeRounding = pick(custom.getEdgeRounding(), defaults.getEdgeRounding());
        if (edgeRounding != null) {
            out.writeAttribute(DR3D, "edge-rounding", edgeRounding);
        }
    }

    private void writeTextProperties(XMLStreamWriter out,
                                     ChartTextProperties defaults,
                                     ChartTextProperties custom) throws XMLStreamException {
        Double fontSize = pick(custom.getFontSizePt(), defaults.getFontSizePt());
        if (fontSize != null) {
            out.writeAttribute(FONT, "font-size", fontSize + "pt");
        }
        Double fontSizeAsian = pick(custom.getFontSizeAsianPt(), defaults.getFontSizeAsianPt());
        if (fontSizeAsian != null) {
            out.writeAttribute(STYLE, "font-size-asian", fontSizeAsian + "pt");
        }
        Double fontSizeComplex = pick(custom.getFontSizeComplexPt(), defaults.getFontSizeComplexPt());
        if (fontSizeComplex != null) {
            out.writeAttribute(STYLE, "font-size-complex", fontSizeComplex + "pt");
        }
        String fontWeight = pick(custom.getFontWeight(), defaults.getFontWeight());
        if (fontWeight != null) {
            out.writeAttribute(FONT, "font-weight", fontWeight);
        }
        String fontStyle = pick(custom.getFontStyle(), defaults.getFontStyle());
        if (fontStyle != null) {
            out.writeAttribute(FONT, "font-style", fontStyle);
        }
        String fontFamily = pick(custom.getFontFamily(), defaults.getFontFamily());
        if (fontFamily != null) {
            out.writeAttribute(STYLE, "font-name", fontFamily);
        }
        Color color = pick(custom.getColor(), defaults.getColor());
        if (color != null) {
            out.writeAttribute(FONT, "color", color.toString());
        }
    }

    private void writeBooleanAttr(XMLStreamWriter out, String ns, String name, Boolean value) throws XMLStreamException {
        if (value != null) {
            out.writeAttribute(ns, name, value ? "true" : "false");
        }
    }

    private <T> T pick(T preferred, T fallback) {
        return preferred != null ? preferred : fallback;
    }

    private boolean isLineChart(Chart chart) {
        if (chart == null) {
            return false;
        }
        String type = chart.getType();
        if (type == null) {
            return false;
        }
        return "line".equalsIgnoreCase(type)
                || "chart:line".equalsIgnoreCase(type)
                || "area".equalsIgnoreCase(type)
                || "chart:area".equalsIgnoreCase(type);
    }

    private boolean hasLineCustomAxes(Chart chart) {
        if (!isLineChart(chart)) {
            return false;
        }
        return chart.getXAxis().getStyle().hasAnyProperties()
                || chart.getYAxis().getStyle().hasAnyProperties();
    }

    private String getXAxisStyleName(Chart chart) {
        if (!isLineChart(chart)) {
            return "ch3";
        }
        return hasLineCustomAxes(chart) ? "ch4x" : "ch4";
    }

    private String getYAxisStyleName(Chart chart) {
        if (!isLineChart(chart)) {
            return "ch4";
        }
        return hasLineCustomAxes(chart) ? "ch4y" : "ch4";
    }

    private ChartStyle buildLineSeriesDefaults() {
        ChartStyle seriesDefaults = new ChartStyle();
        seriesDefaults.getChartProperties().setSymbolType("named-symbol");
        seriesDefaults.getChartProperties().setSymbolName("square");
        seriesDefaults.getChartProperties().setSymbolWidth("0.25cm");
        seriesDefaults.getChartProperties().setSymbolHeight("0.25cm");
        seriesDefaults.getChartProperties().setLinkDataStyleToSource(true);
        seriesDefaults.getGraphicProperties().setStrokeWidth("0.08cm");
        seriesDefaults.getGraphicProperties().setStrokeColor(new Color("#004586"));
        seriesDefaults.getGraphicProperties().setFillColor(new Color("#004586"));
        seriesDefaults.getGraphicProperties().setEdgeRounding("5%");
        seriesDefaults.getTextProperties().setFontSizePt(10);
        seriesDefaults.getTextProperties().setFontSizeAsianPt(10);
        seriesDefaults.getTextProperties().setFontSizeComplexPt(10);
        return seriesDefaults;
    }

    private ChartStyle buildBarSeriesDefaults() {
        ChartStyle seriesDefaults = new ChartStyle();
        seriesDefaults.getChartProperties().setLinkDataStyleToSource(true);
        seriesDefaults.getGraphicProperties().setStroke("none");
        seriesDefaults.getGraphicProperties().setFillColor(new Color("#004586"));
        seriesDefaults.getGraphicProperties().setEdgeRounding("5%");
        seriesDefaults.getTextProperties().setFontSizePt(10);
        seriesDefaults.getTextProperties().setFontSizeAsianPt(10);
        seriesDefaults.getTextProperties().setFontSizeComplexPt(10);
        return seriesDefaults;
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
