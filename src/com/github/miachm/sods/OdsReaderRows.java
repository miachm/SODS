package com.github.miachm.sods;

class OdsReaderRows extends OdsReaderExtensionAbstract {

    OdsReaderRows(OdsReader reader)
    {
        super("table:table-row", reader);
    }

    @Override
    public void processTag(XmlReaderInstance instance) {
        int cnt = 1;
        String value = instance.getAttribValue("table:number-rows-spanned");
        try {
            cnt = Integer.parseInt(value);
        }
        catch (Exception e) {}
        reader.getCurrentSheet().appendRows(cnt);
    }
}
