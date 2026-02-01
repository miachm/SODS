package com.github.miachm.sods;

import java.util.Arrays;

/**
 * Represents an image anchored to a sheet cell.
 */
public class SheetImage {
    private String path;
    private String name;
    private String mimeType;
    private byte[] data;
    private String width;
    private String height;
    private String x;
    private String y;
    private Integer anchorRow;
    private Integer anchorColumn;

    public SheetImage(byte[] data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
    }

    SheetImage(String path, String mimeType, byte[] data) {
        this.path = path;
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getData() {
        return data == null ? null : Arrays.copyOf(data, data.length);
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

    public Integer getAnchorRow() {
        return anchorRow;
    }

    public Integer getAnchorColumn() {
        return anchorColumn;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAnchorRow(Integer anchorRow) {
        this.anchorRow = anchorRow;
    }

    public void setAnchorColumn(Integer anchorColumn) {
        this.anchorColumn = anchorColumn;
    }

    void setPath(String path) {
        this.path = path;
    }

    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    void setData(byte[] data) {
        this.data = data;
    }

    byte[] getDataInternal() {
        return data;
    }
}
