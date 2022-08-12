package com.github.miachm.sods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class OdsReaderValues implements OdsReaderExtension {
    Map<String, String[]> tags = new HashMap<>();
    Map<String, Set<String>> atributes = new HashMap<>();
    OdsReader reader;

    OdsReaderValues(OdsReader reader)
    {
        this.reader = reader;
        tags.put("", new String[]{"office:body"});
        tags.put("<<office:body", new String[]{"office:spreadsheet"});
        tags.put("<<office:body<<office:spreadsheet", new String[]{"table:table"});
        tags.put("<<office:body<<office:spreadsheet<<table:table", new String[]{"table:row"});
        tags.put("<<office:body<<office:spreadsheet<<table:table<<table:row", new String[]{"table:table-cell"});

        Set<String> mySet = new HashSet<>();
        mySet.add("table:formula");
        atributes.put("<<office:body<<office:spreadsheet<<table:table<<table:row<<table:table-cell", mySet);
    }

    @Override
    public String[] managedTags(String path) {
        return tags.getOrDefault(path, new String[0]);
    }

    @Override
    public Set<String> managedAtributes(String path) {
        return atributes.getOrDefault(path, new HashSet<>());
    }

    @Override
    public void readAtribute(String key, String value) {

    }

    @Override
    public void readContent(String path, XmlReaderInstance instance) {
        if (path.equals("<<office:body<<office:spreadsheet<<table:table<<table:row<<table:table-cell")) {
            OfficeValueType valueType = OfficeValueType.ofReader(instance);
            Object value = valueType.read(instance);
            reader.getCurrentRange().setValue(value);
        }
    }
}
