package com.github.miachm.sods;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * Represents a value linked to a sheet, file, URI or URL.
 */
public class LinkedValue {
    private final String href;
    private final String text;

    private LinkedValue(String href, String text) {
        this.href = href;
        this.text = text;
    }

    /**
     * Creates a value linking to a cell of the given sheet.
     *
     * @param text  the text to display for the link
     * @param sheet the sheet to link to
     */
    public LinkedValue(String text, Sheet sheet) {
        this('#' + sheet.getName() + ".A1", text);
    }

    /**
     * Creates a value linking to the given file.
     *
     * @param text the text to display for the link
     * @param file the file to link to
     */
    public LinkedValue(String text, File file) {
        this(file.toURI().toString(), text);
    }

    /**
     * Creates a value linking to the given URL.
     *
     * @param text the text to display for the link
     * @param url  the URL to link to
     */
    public LinkedValue(String text, URL url) {
        this(url.toString(), text);
    }

    /**
     * Creates a value linking to the given URI.
     *
     * @param text the text to display for the link
     * @param uri  the URI to link to
     */
    public LinkedValue(String text, URI uri) {
        this(uri.toString(), text);
    }

    String getHref() {
        return href;
    }

    String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedValue that = (LinkedValue) o;
        return Objects.equals(href, that.href) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, text);
    }

    @Override
    public String toString() {
        return "LinkedValue{" +
                "href='" + href + '\'' +
                ", text=" + text +
                '}';
    }
}
