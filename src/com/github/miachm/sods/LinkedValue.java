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
    private final String value;

    LinkedValue(String href, String value) {
        this.href = href;
        this.value = value;
    }

    public static LinkedValue.Builder builder() {
        return new LinkedValue.Builder();
    }

    String getHref() {
        return href;
    }

    String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedValue that = (LinkedValue) o;
        return Objects.equals(href, that.href) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, value);
    }

    @Override
    public String toString() {
        return "LinkedValue{" +
                "href='" + href + '\'' +
                ", value=" + value +
                '}';
    }

    public static class Builder {
        private String href;
        private String value;

        public LinkedValue build() {
            return new LinkedValue(href, value);
        }

        public LinkedValue.Builder value(String value) {
            this.value = value;
            return this;
        }

        public LinkedValue.Builder href(Sheet sheet) {
            this.href = '#' + sheet.getName() + ".A1";
            // this.href = '#' + sheet.getName(); // TODO: remove this - not compatible with Excel
            return this;
        }

        public LinkedValue.Builder href(File file) {
            this.href = file.toURI().toString();
            return this;
        }

        public LinkedValue.Builder href(URL url) {
            this.href = url.toString();
            return this;
        }

        public LinkedValue.Builder href(URI uri) {
            this.href = uri.toString();
            return this;
        }
    }
}
