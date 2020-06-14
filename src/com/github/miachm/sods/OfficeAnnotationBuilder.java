package com.github.miachm.sods;

import java.time.LocalDateTime;

/**
 * A helper class whose objetive is to build a OfficeAnnotation class
 *
 * @see OfficeAnnotation
 */
public class OfficeAnnotationBuilder {
    private String msg;
    private LocalDateTime lastModified;

    public OfficeAnnotation build()
    {
        return new OfficeAnnotation(msg, lastModified);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
