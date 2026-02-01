package com.github.miachm.sods;

import java.util.Arrays;

/**
 * Represents an image embedded in a {@link Sheet}.
 *
 * Images can be anchored to a cell position with an offset and size. This
 * object stores the raw bytes and the layout information used by the writer.
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

    /**
     * Creates an image from raw bytes and MIME type.
     *
     * The image data is copied on access and stored as-is for serialization
     * into the ODS package.
     *
     * @param data The image bytes.
     * @param mimeType The image MIME type.
     */
    public SheetImage(byte[] data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
    }

    SheetImage(String path, String mimeType, byte[] data) {
        this.path = path;
        this.mimeType = mimeType;
        this.data = data;
    }

    /**
     * Returns the internal path for this image.
     *
     * The path is used inside the ODS package to reference the image entry.
     *
     * @return The path, or null if not assigned.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the image name.
     *
     * Names are used to identify the image object inside the document.
     *
     * @return The image name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the image MIME type.
     *
     * This value indicates the image encoding, such as "image/png".
     *
     * @return The MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns a copy of the image bytes.
     *
     * A defensive copy is returned to prevent external modifications.
     *
     * @return The image bytes, or null if unset.
     */
    public byte[] getData() {
        return data == null ? null : Arrays.copyOf(data, data.length);
    }

    /**
     * Returns the image width.
     *
     * This is a serialized size value (for example, "3.5cm") stored for layout.
     *
     * @return The width value.
     */
    public String getWidth() {
        return width;
    }

    /**
     * Returns the image height.
     *
     * This is a serialized size value (for example, "2.0cm") stored for layout.
     *
     * @return The height value.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Returns the X position.
     *
     * This is the horizontal offset from the anchor cell.
     *
     * @return The X position value.
     */
    public String getX() {
        return x;
    }

    /**
     * Returns the Y position.
     *
     * This is the vertical offset from the anchor cell.
     *
     * @return The Y position value.
     */
    public String getY() {
        return y;
    }

    /**
     * Returns the anchor row index.
     *
     * The anchor is the top-left cell where the image is positioned.
     *
     * @return The anchor row.
     */
    public Integer getAnchorRow() {
        return anchorRow;
    }

    /**
     * Returns the anchor column index.
     *
     * The anchor is the top-left cell where the image is positioned.
     *
     * @return The anchor column.
     */
    public Integer getAnchorColumn() {
        return anchorColumn;
    }

    /**
     * Sets the image name.
     *
     * The name is used to identify the image object inside the document.
     *
     * @param name The image name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the image width.
     *
     * The value is stored as a serialized size (for example, "3.5cm").
     *
     * @param width The width value.
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Sets the image height.
     *
     * The value is stored as a serialized size (for example, "2.0cm").
     *
     * @param height The height value.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Sets the X position.
     *
     * This is the horizontal offset from the anchor cell.
     *
     * @param x The X position value.
     */
    public void setX(String x) {
        this.x = x;
    }

    /**
     * Sets the Y position.
     *
     * This is the vertical offset from the anchor cell.
     *
     * @param y The Y position value.
     */
    public void setY(String y) {
        this.y = y;
    }

    /**
     * Sets the anchor row index.
     *
     * The anchor is the top-left cell where the image is positioned.
     *
     * @param anchorRow The anchor row.
     */
    public void setAnchorRow(Integer anchorRow) {
        this.anchorRow = anchorRow;
    }

    /**
     * Sets the anchor column index.
     *
     * The anchor is the top-left cell where the image is positioned.
     *
     * @param anchorColumn The anchor column.
     */
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
