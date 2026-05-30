package com.github.miachm.sods;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Load options for reading a Spreadsheet
 */

public class OdsOptionParameters {
    private boolean load_styles = true;
    private boolean load_images = true;
    private boolean load_graphs = true;
    private boolean load_macros = true;
    private List<Integer> sheetNumbers;
    private String password;
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
     * If it should load images or not.
     * @return True if it should load images
     */
    public boolean isLoadImages() {
        return load_images;
    }

    public void setLoadImages(boolean load_images) {
        this.load_images = load_images;
    }

    /**
     * If it should load charts/graphs or not.
     * @return True if it should load charts/graphs
     */
    public boolean isLoadGraphs() {
        return load_graphs;
    }

    public void setLoadGraphs(boolean load_graphs) {
        this.load_graphs = load_graphs;
    }

    /**
     * If it should load Basic macros or not.
     * When {@code false}, all {@code Basic/} entries inside the ODS ZIP are
     * skipped and the resulting {@link SpreadSheet} will have no macros.
     *
     * @return {@code true} if macros should be loaded (the default)
     */
    public boolean isLoadMacros() {
        return load_macros;
    }

    public void setLoadMacros(boolean load_macros) {
        this.load_macros = load_macros;
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

    /**
     * Password for opening an encrypted ODF package (document-level encryption).
     * Required when the file was saved with a document password; ignored for unencrypted files.
     *
     * @param password the password, or {@code null} if not encrypted
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password used to decrypt the package, or {@code null}
     */
    public String getPassword() {
        return password;
    }
}
