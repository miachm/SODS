package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Chart {
    private String type;
    private String title;
    private String footer;
    private String legend;
    private final List<String> data = new ArrayList<>();
    private final List<Object> categories = new ArrayList<>();
    private String categoriesRangeAddress;
    private final List<ChartSeries> series = new ArrayList<>();
    private Sheet sheet;

    private String width;
    private String height;
    private String x;
    private String y;
    private String xAxisLabel;
    private String yAxisLabel;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getFooter() {
        return footer;
    }

    public String getLegend() {
        return legend;
    }

    public List<String> getData() {
        return data;
    }

    public List<Object> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public String getCategoriesRangeAddress() {
        return categoriesRangeAddress;
    }

    public List<ChartSeries> getSeries() {
        return Collections.unmodifiableList(series);
    }

    public Sheet getSheet() {
        return sheet;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public void addData(String data) {
        this.data.add(data);
    }

    public void addCategory(Object category) {
        this.categories.add(category);
    }

    public void setCategoriesRangeAddress(String categoriesRangeAddress) {
        this.categoriesRangeAddress = categoriesRangeAddress;
    }

    public void addSeries(ChartSeries series) {
        if (series != null) {
            this.series.add(series);
        }
    }

    void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }
}
