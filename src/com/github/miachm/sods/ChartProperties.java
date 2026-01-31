package com.github.miachm.sods;

/**
 * Properties applied under style:chart-properties for chart elements.
 */
public class ChartProperties {
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

    public Boolean getDisplayLabel() {
        return displayLabel;
    }

    public Boolean getLogarithmic() {
        return logarithmic;
    }

    public Boolean getReverseDirection() {
        return reverseDirection;
    }

    public Boolean getLinkDataStyleToSource() {
        return linkDataStyleToSource;
    }

    public Boolean getAutoPosition() {
        return autoPosition;
    }

    public Boolean getAutoSize() {
        return autoSize;
    }

    public Boolean getRightAngledAxes() {
        return rightAngledAxes;
    }

    public Boolean getIncludeHiddenCells() {
        return includeHiddenCells;
    }

    public Boolean getTextLineBreak() {
        return textLineBreak;
    }

    public Boolean getTryStaggeringFirst() {
        return tryStaggeringFirst;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public String getSymbolWidth() {
        return symbolWidth;
    }

    public String getSymbolHeight() {
        return symbolHeight;
    }

    public TreatEmptyCells getTreatEmptyCells() {
        return treatEmptyCells;
    }

    public Integer getAxisPosition() {
        return axisPosition;
    }

    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    public void setLogarithmic(boolean logarithmic) {
        this.logarithmic = logarithmic;
    }

    public void setReverseDirection(boolean reverseDirection) {
        this.reverseDirection = reverseDirection;
    }

    public void setLinkDataStyleToSource(boolean linkDataStyleToSource) {
        this.linkDataStyleToSource = linkDataStyleToSource;
    }

    public void setAutoPosition(boolean autoPosition) {
        this.autoPosition = autoPosition;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public void setRightAngledAxes(boolean rightAngledAxes) {
        this.rightAngledAxes = rightAngledAxes;
    }

    public void setIncludeHiddenCells(boolean includeHiddenCells) {
        this.includeHiddenCells = includeHiddenCells;
    }

    public void setTextLineBreak(boolean textLineBreak) {
        this.textLineBreak = textLineBreak;
    }

    public void setTryStaggeringFirst(boolean tryStaggeringFirst) {
        this.tryStaggeringFirst = tryStaggeringFirst;
    }

    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public void setSymbolWidth(String symbolWidth) {
        this.symbolWidth = symbolWidth;
    }

    public void setSymbolHeight(String symbolHeight) {
        this.symbolHeight = symbolHeight;
    }

    public void setTreatEmptyCells(TreatEmptyCells treatEmptyCells) {
        this.treatEmptyCells = treatEmptyCells;
    }

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
