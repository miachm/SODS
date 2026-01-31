package com.github.miachm.sods;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Load options for reading a Spreadsheet
 */

public class OdsOptionParameters {
    private boolean load_styles = true;
    private List<Integer> sheetNumbers;
    private Logger logger;

    public OdsOptionParameters() {
        this.logger = Logger.getLogger("ODS_Reader");
        this.logger.setLevel(Level.SEVERE);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        if (logger == null) {
            logger = Logger.getLogger("ODS_Reader");
            logger.setLevel(Level.SEVERE);
        }
        this.logger = logger;
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

    /**
     * Gets the list of sheet numbers to load. If null, all sheets are loaded.
     * @return List of sheet numbers (0-based) to load, or null for all sheets
     */
    public List<Integer> getSheetNumbers() {
        return sheetNumbers;
    }

    /**
     * Sets the list of sheet numbers to load. If null, all sheets are loaded.
     * @param sheetNumbers List of sheet numbers (0-based) to load, or null for all sheets
     */
    public void setSheetNumbers(List<Integer> sheetNumbers) {
        this.sheetNumbers = sheetNumbers;
    }
}