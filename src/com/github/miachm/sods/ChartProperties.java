package com.github.miachm.sods;

/**
 * Holds chart-specific properties.
 *
 * These options map to ODS chart settings and control aspects like axis
 * positioning, label handling, symbol rendering, and layout behavior.
 */
public class ChartProperties {
    /**
     * How empty cells are treated in chart data.
     */
    public enum TreatEmptyCells {
        LeaveGap("leave-gap"),
        UseZero("use-zero"),
        Ignore("ignore");

        private final String value;

        TreatEmptyCells(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private Boolean displayLabel = true;
    private Boolean logarithmic;
    private Boolean reverseDirection;
    private Boolean linkDataStyleToSource;
    private Boolean autoPosition;
    private Boolean autoSize;
    private Boolean rightAngledAxes;
    private Boolean includeHiddenCells;
    private Boolean textLineBreak;
    private Boolean tryStaggeringFirst;
    private String symbolType;
    private String symbolName;
    private String symbolWidth;
    private String symbolHeight;
    private TreatEmptyCells treatEmptyCells;
    private Integer axisPosition;

    /**
     * Returns whether labels are displayed.
     *
     * Labels are typically value labels or series labels that can be rendered
     * directly on the chart.
     *
     * @return True if labels are displayed.
     */
    public Boolean getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Returns whether logarithmic scaling is enabled.
     *
     * When enabled, the axis is scaled logarithmically instead of linearly.
     *
     * @return True if logarithmic scaling is enabled.
     */
    public Boolean getLogarithmic() {
        return logarithmic;
    }

    /**
     * Returns whether the axis direction is reversed.
     *
     * Reversed axes display values in the opposite direction.
     *
     * @return True if the axis direction is reversed.
     */
    public Boolean getReverseDirection() {
        return reverseDirection;
    }

    /**
     * Returns whether data style is linked to the source.
     *
     * When enabled, the chart follows the data style defined in the sheet.
     *
     * @return True if linked to source.
     */
    public Boolean getLinkDataStyleToSource() {
        return linkDataStyleToSource;
    }

    /**
     * Returns whether auto-positioning is enabled.
     *
     * Auto-positioning allows the document engine to place elements
     * automatically within the chart area.
     *
     * @return True if auto-positioning is enabled.
     */
    public Boolean getAutoPosition() {
        return autoPosition;
    }

    /**
     * Returns whether auto-sizing is enabled.
     *
     * Auto-size allows the document engine to size elements based on content.
     *
     * @return True if auto-sizing is enabled.
     */
    public Boolean getAutoSize() {
        return autoSize;
    }

    /**
     * Returns whether right-angled axes are enabled.
     *
     * When enabled, axes are forced to render at right angles.
     *
     * @return True if right-angled axes are enabled.
     */
    public Boolean getRightAngledAxes() {
        return rightAngledAxes;
    }

    /**
     * Returns whether hidden cells are included.
     *
     * When enabled, hidden rows/columns can still contribute values to the chart.
     *
     * @return True if hidden cells are included.
     */
    public Boolean getIncludeHiddenCells() {
        return includeHiddenCells;
    }

    /**
     * Returns whether text line breaks are enabled.
     *
     * This affects how multi-line labels are rendered.
     *
     * @return True if text line breaks are enabled.
     */
    public Boolean getTextLineBreak() {
        return textLineBreak;
    }

    /**
     * Returns whether to try staggering labels first.
     *
     * Staggering is used to reduce label overlap by alternating positions.
     *
     * @return True if staggering is preferred.
     */
    public Boolean getTryStaggeringFirst() {
        return tryStaggeringFirst;
    }

    /**
     * Returns the symbol type.
     *
     * This describes the marker style used for points (for example, circle or square).
     *
     * @return The symbol type.
     */
    public String getSymbolType() {
        return symbolType;
    }

    /**
     * Returns the symbol name.
     *
     * The name corresponds to an ODS symbol identifier.
     *
     * @return The symbol name.
     */
    public String getSymbolName() {
        return symbolName;
    }

    /**
     * Returns the symbol width.
     *
     * This is a serialized size value (for example, "0.2cm").
     *
     * @return The symbol width.
     */
    public String getSymbolWidth() {
        return symbolWidth;
    }

    /**
     * Returns the symbol height.
     *
     * This is a serialized size value (for example, "0.2cm").
     *
     * @return The symbol height.
     */
    public String getSymbolHeight() {
        return symbolHeight;
    }

    /**
     * Returns the empty-cell behavior.
     *
     * This determines whether empty cells are ignored, treated as zero,
     * or rendered as gaps in the chart.
     *
     * @return The empty-cell behavior.
     */
    public TreatEmptyCells getTreatEmptyCells() {
        return treatEmptyCells;
    }

    /**
     * Returns the axis position index.
     *
     * This value indicates where the axis intersects relative to the plot area.
     *
     * @return The axis position.
     */
    public Integer getAxisPosition() {
        return axisPosition;
    }

    /**
     * Sets whether labels are displayed.
     *
     * Labels are typically value labels or series labels shown on the chart.
     *
     * @param displayLabel True to display labels.
     */
    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    /**
     * Sets whether logarithmic scaling is enabled.
     *
     * When enabled, the axis is scaled logarithmically.
     *
     * @param logarithmic True to enable logarithmic scaling.
     */
    public void setLogarithmic(boolean logarithmic) {
        this.logarithmic = logarithmic;
    }

    /**
     * Sets whether the axis direction is reversed.
     *
     * Reversed axes display values in the opposite direction.
     *
     * @param reverseDirection True to reverse axis direction.
     */
    public void setReverseDirection(boolean reverseDirection) {
        this.reverseDirection = reverseDirection;
    }

    /**
     * Sets whether data style is linked to the source.
     *
     * When enabled, the chart follows the data style defined in the sheet.
     *
     * @param linkDataStyleToSource True to link to source.
     */
    public void setLinkDataStyleToSource(boolean linkDataStyleToSource) {
        this.linkDataStyleToSource = linkDataStyleToSource;
    }

    /**
     * Sets whether auto-positioning is enabled.
     *
     * Auto-positioning lets the document engine place elements automatically.
     *
     * @param autoPosition True to enable auto-positioning.
     */
    public void setAutoPosition(boolean autoPosition) {
        this.autoPosition = autoPosition;
    }

    /**
     * Sets whether auto-sizing is enabled.
     *
     * Auto-size lets the document engine size elements based on content.
     *
     * @param autoSize True to enable auto-sizing.
     */
    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    /**
     * Sets whether right-angled axes are enabled.
     *
     * When enabled, axes are forced to render at right angles.
     *
     * @param rightAngledAxes True to enable right-angled axes.
     */
    public void setRightAngledAxes(boolean rightAngledAxes) {
        this.rightAngledAxes = rightAngledAxes;
    }

    /**
     * Sets whether hidden cells are included.
     *
     * Hidden rows/columns can still contribute values to the chart when enabled.
     *
     * @param includeHiddenCells True to include hidden cells.
     */
    public void setIncludeHiddenCells(boolean includeHiddenCells) {
        this.includeHiddenCells = includeHiddenCells;
    }

    /**
     * Sets whether text line breaks are enabled.
     *
     * This affects how multi-line labels are rendered.
     *
     * @param textLineBreak True to enable text line breaks.
     */
    public void setTextLineBreak(boolean textLineBreak) {
        this.textLineBreak = textLineBreak;
    }

    /**
     * Sets whether to try staggering labels first.
     *
     * Staggering is used to reduce label overlap by alternating positions.
     *
     * @param tryStaggeringFirst True to try staggering first.
     */
    public void setTryStaggeringFirst(boolean tryStaggeringFirst) {
        this.tryStaggeringFirst = tryStaggeringFirst;
    }

    /**
     * Sets the symbol type.
     *
     * This describes the marker style used for points (for example, circle or square).
     *
     * @param symbolType The symbol type.
     */
    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    /**
     * Sets the symbol name.
     *
     * The name corresponds to an ODS symbol identifier.
     *
     * @param symbolName The symbol name.
     */
    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Sets the symbol width.
     *
     * The value is stored as a size string in the document.
     *
     * @param symbolWidth The symbol width.
     */
    public void setSymbolWidth(String symbolWidth) {
        this.symbolWidth = symbolWidth;
    }

    /**
     * Sets the symbol height.
     *
     * The value is stored as a size string in the document.
     *
     * @param symbolHeight The symbol height.
     */
    public void setSymbolHeight(String symbolHeight) {
        this.symbolHeight = symbolHeight;
    }

    /**
     * Sets how empty cells are treated.
     *
     * This determines whether empty cells are ignored, treated as zero,
     * or rendered as gaps.
     *
     * @param treatEmptyCells The empty-cell behavior.
     */
    public void setTreatEmptyCells(TreatEmptyCells treatEmptyCells) {
        this.treatEmptyCells = treatEmptyCells;
    }

    /**
     * Sets the axis position index.
     *
     * This value indicates where the axis intersects relative to the plot area.
     *
     * @param axisPosition The axis position.
     */
    public void setAxisPosition(int axisPosition) {
        this.axisPosition = axisPosition;
    }

    boolean hasAny() {
        return displayLabel != null
                || logarithmic != null
                || reverseDirection != null
                || linkDataStyleToSource != null
                || autoPosition != null
                || autoSize != null
                || rightAngledAxes != null
                || includeHiddenCells != null
                || textLineBreak != null
                || tryStaggeringFirst != null
                || symbolType != null
                || symbolName != null
                || symbolWidth != null
                || symbolHeight != null
                || treatEmptyCells != null
                || axisPosition != null;
    }
}
