package com.github.miachm.sods;

class OdsReaderSheets extends OdsReaderExtensionAbstract {

    OdsReaderSheets(OdsReader reader)
    {
        super("table:table", reader);
    }

    @Override
    public void processTag(XmlReaderInstance instance) {
        String name = instance.getAttribValue("table:name");
        if (name == null)
            name = "Unknown Name";

        Sheet sheet = new Sheet(name, 0, 0);
        String protectedSheet = instance.getAttribValue("table:protected");
        if (protectedSheet != null) {
            String algorithm = instance.getAttribValue("table:protection-key-digest-algorithm");
            if (algorithm == null)
                algorithm = "http://www.w3.org/2000/09/xmldsig#sha1";

            String protectedKey = instance.getAttribValue("table:protection-key");
            sheet.setRawPassword(protectedKey, algorithm);
        }

        reader.addSheet(sheet);
    }
}
