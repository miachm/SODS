package com.github.miachm.sods;

interface XmlReaderInstance {
    boolean hasNext();
    XmlReaderInstance nextElement(String... name);
    String getAttribValue(String name);
    String getContent();
    String getTag();
}
