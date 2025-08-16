package com.github.miachm.sods;

public class OdsOptionParameters {
    private boolean load_styles = true;

    public OdsOptionParameters() {
    }

    /**
     * If it should load Formatting styles or not.
     * @return True if it load formatting style
     */
    public boolean isLoadStyles() {
        return load_styles;
    }

    public void setLoadStyles(boolean load_styles) {
        this.load_styles = load_styles;
    }
}