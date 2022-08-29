package com.github.miachm.sods;

interface OdsReaderExtension {
    String tag();
    void processTag(XmlReaderInstance instance);
}
