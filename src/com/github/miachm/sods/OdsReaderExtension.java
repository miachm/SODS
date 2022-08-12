package com.github.miachm.sods;


import java.util.Set;

interface OdsReaderExtension {

    String[] managedTags(String path);
    Set<String> managedAtributes(String path);
    void readAtribute(String key, String value);

    void readContent(String path, XmlReaderInstance instance);
}
