package com.github.miachm.sods;

class OdsReaderColumns extends OdsReaderExtensionAbstract {

    OdsReaderColumns(OdsReader reader)
    {
        super("table:table-column", reader);
    }

    @Override
    public void processTag(XmlReaderInstance instance) {
        int cnt = 1;
        String value = instance.getAttribValue("table:number-columns-spanned");
        try {
            cnt = Integer.parseInt(value);
        }
        catch (Exception e) {}
        reader.getCurrentSheet().appendColumns(cnt);
    }
}
