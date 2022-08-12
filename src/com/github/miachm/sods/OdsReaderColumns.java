package com.github.miachm.sods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OdsReaderColumns implements OdsReaderExtension {
    Map<String, String[]> tags = new HashMap<>();
    Map<String, Set<String>> atributes = new HashMap<>();
    OdsReader reader;

    OdsReaderColumns(OdsReader reader)
    {
        this.reader = reader;
        tags.put("", new String[]{"office:body"});
        tags.put("<<office:body", new String[]{"office:spreadsheet"});
        tags.put("<<office:body<<office:spreadsheet", new String[]{"table:table"});
        tags.put("<<office:body<<office:spreadsheet<<table:table", new String[]{"table:column"});

        Set<String> mySet = new HashSet<>();
        mySet.add("table:formula");
        atributes.put("<<office:body<<office:spreadsheet<<table:table<<table:column", mySet);
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
        int cnt = 1;
        try {
            cnt = Integer.parseInt(value);
        }
        reader.getCurrentSheet().appendColumns();
    }

    @Override
    public void readContent(String path, XmlReaderInstance instance) {

    }
}
