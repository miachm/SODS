package com.github.miachm.sods;

interface XmlReaderInstance {
    String CHARACTERS = "characters";

    boolean hasNext();
    XmlReaderInstance nextElement(String... name);
    String getAttribValue(String name);
    String getContent();
    String getTag();
}
