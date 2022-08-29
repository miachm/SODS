package com.github.miachm.sods;

abstract class OdsReaderExtensionAbstract implements OdsReaderExtension {
    protected String tag;
    protected OdsReader reader;

    OdsReaderExtensionAbstract(String tag, OdsReader reader)
    {
        this.tag = tag;
        this.reader = reader;
    }

    @Override
    public String tag()
    {
        return tag;
    }
}
