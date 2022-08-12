package com.github.miachm.sods;

import java.util.List;
import java.util.Map;

interface XmlReaderInstance {
    String CHARACTERS = "characters";

    boolean hasNext();
    XmlReaderInstance nextElement(String... name);
    String getAttribValue(String name);
    Map<String, String> getAllAttributes();
    String getContent();
    String getTag();
}
