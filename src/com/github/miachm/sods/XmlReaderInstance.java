package com.github.miachm.sods;

import java.util.List;
import java.util.Map;
import java.util.Set;

interface XmlReaderInstance {
    String CHARACTERS = "characters";

    boolean hasNext();
    XmlReaderInstance nextElement(String... name);
    XmlReaderInstance nextElement(Set<String> name);
    String getAttribValue(String name);
    String getContent();
    String getTag();
}
