package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChartObjectRegistry {
    private final Map<String, Sheet> chartObjectSheets = new HashMap<>();
    private final Map<String, List<Chart>> pendingCharts = new HashMap<>();
    private final Map<String, ChartFrame> chartObjectFrames = new HashMap<>();

    void registerChartObject(String objectPath, Sheet sheet, ChartFrame frame) {
        if (objectPath != null && sheet != null) {
            chartObjectSheets.put(objectPath, sheet);
            if (frame != null) {
                chartObjectFrames.put(objectPath, frame);
            }
            List<Chart> pending = pendingCharts.remove(objectPath);
            if (pending != null) {
                for (Chart chart : pending) {
                    if (chart != null && chart.getSheet() == null) {
                        applyFrame(chart, chartObjectFrames.get(objectPath));
                        sheet.addChart(chart);
                    }
                }
            }
        }
    }

    void addChart(Chart chart, String objectPath) {
        if (chart == null || objectPath == null) {
            return;
        }
        applyFrame(chart, chartObjectFrames.get(objectPath));
        Sheet sheet = chartObjectSheets.get(objectPath);
        if (sheet != null) {
            if (chart.getSheet() == null) {
                sheet.addChart(chart);
            }
            return;
        }
        pendingCharts.computeIfAbsent(objectPath, key -> new ArrayList<>()).add(chart);
    }

    Sheet getChartSheet(String objectPath) {
        if (objectPath == null) {
            return null;
        }
        return chartObjectSheets.get(objectPath);
    }

    private void applyFrame(Chart chart, ChartFrame frame) {
        if (chart == null || frame == null) {
            return;
        }
        if (frame.x != null) chart.setX(frame.x);
        if (frame.y != null) chart.setY(frame.y);
        if (frame.width != null) chart.setWidth(frame.width);
        if (frame.height != null) chart.setHeight(frame.height);
    }

    static class ChartFrame {
        final String x;
        final String y;
        final String width;
        final String height;

        ChartFrame(String x, String y, String width, String height) {
            this.x = normalizeValue(x);
            this.y = normalizeValue(y);
            this.width = normalizeValue(width);
            this.height = normalizeValue(height);
        }

        private static String normalizeValue(String value) {
            if (value == null) return null;
            String trimmed = value.trim();
            return trimmed.isEmpty() ? null : trimmed;
        }
    }
}
