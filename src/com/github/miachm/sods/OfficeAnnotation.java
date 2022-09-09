package com.github.miachm.sods;

import java.time.LocalDateTime;

/**
 * This class represent an Annotation in a Cell of the Sheet.
 *
 * It can not be instanced by itself, you should use OfficeAnnotationBuilder
 *
 * @see OfficeAnnotationBuilder
 */
public class OfficeAnnotation implements Cloneable {
    private final String msg;
    private final LocalDateTime lastModified;

    public OfficeAnnotation()
    {
        msg = null;
        lastModified = null;
    }

    public OfficeAnnotation(String msg, LocalDateTime lastModified)
    {
        this.msg = msg;
        this.lastModified = lastModified;
    }

    /**
     * Returns the msg contained in the comment
     *
     * @return The msg in the comment
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Get the date of the comment (last modification date)
     *
     * @return The date, or null if it is not known
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfficeAnnotation that = (OfficeAnnotation) o;

        if (msg != null ? !msg.equals(that.msg) : that.msg != null) return false;
        return lastModified != null ? lastModified.equals(that.lastModified) : that.lastModified == null;
    }

    @Override
    public int hashCode() {
        int result = msg != null ? msg.hashCode() : 0;
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return msg;
    }
}
