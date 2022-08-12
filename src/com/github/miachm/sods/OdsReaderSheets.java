package com.github.miachm.sods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OdsReaderSheets implements OdsReaderExtension {
    Map<String, String[]> tags = new HashMap<>();
    Map<String, Set<String>> atributes = new HashMap<>();
    OdsReader reader;

    OdsReaderSheets(OdsReader reader)
    {
        this.reader = reader;
        tags.put("", new String[]{"office:body"});
        tags.put("<<office:body", new String[]{"office:spreadsheet"});
        tags.put("<<office:body<<office:spreadsheet", new String[]{"table:table"});
        tags.put("<<office:body<<office:spreadsheet<<table:table", new String[]{"table:row"});

        Set<String> mySet = new HashSet<>();
        mySet.add("table:name");
        atributes.put("<<office:body<<office:spreadsheet<<table:table", mySet);
    }

    @Override
    public String[] managedTags(String path) {
        return new String[0];
    }

    @Override
    public Set<String> managedAtributes(String path) {
        return atributes.getOrDefault(path, new HashSet<>());
    }

    @Override
    public void readAtribute(String key, String value) {
        if (key.equals("table:name")) {
            reader.addSheet(new Sheet(value));
        }
    }

    @Override
    public void readContent(String path, XmlReaderInstance instance) {

    }
}
