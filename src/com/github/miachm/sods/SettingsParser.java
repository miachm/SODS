package com.github.miachm.sods;

class SettingsParser {
    private final SpreadSheet spread;

    SettingsParser(SpreadSheet spread) {
        this.spread = spread;
    }

    void parseSettings(XmlReaderInstance root) {
        XmlReaderInstance settingsEl = root.nextElement("office:settings");
        if (settingsEl == null) return;

        // Skip directly to the Views indexed map — it's always inside ooo:view-settings
        XmlReaderInstance viewsMap = settingsEl.nextElement("config:config-item-map-indexed");
        if (viewsMap == null) return;

        // Only the first view entry matters
        XmlReaderInstance viewEntry = viewsMap.nextElement("config:config-item-map-entry");
        if (viewEntry == null) return;

        XmlReaderInstance tablesMap = viewEntry.nextElement("config:config-item-map-named");
        if (tablesMap == null) return;

        while (tablesMap.hasNext()) {
            XmlReaderInstance sheetEntry = tablesMap.nextElement("config:config-item-map-entry");
            if (sheetEntry == null) break;

            String sheetName = sheetEntry.getAttribValue("config:name");
            if (sheetName == null) continue;

            Sheet sheet = findSheet(sheetName);
            if (sheet == null) continue;

            parseSheetSettings(sheetEntry, sheet);
        }
    }

    private void parseSheetSettings(XmlReaderInstance sheetEntry, Sheet sheet) {
        int hSplitMode = 0, vSplitMode = 0;
        int hSplitPos = 0, vSplitPos = 0;

        while (sheetEntry.hasNext()) {
            XmlReaderInstance item = sheetEntry.nextElement("config:config-item");
            if (item == null) break;

            String name = item.getAttribValue("config:name");
            XmlReaderInstance chars = item.nextElement(XmlReaderInstance.CHARACTERS);
            if (name == null || chars == null) continue;

            String value = chars.getContent();
            if (value == null) continue;

            try {
                int v = Integer.parseInt(value.trim());
                switch (name) {
                    case "HorizontalSplitMode":     hSplitMode = v; break;
                    case "VerticalSplitMode":       vSplitMode = v; break;
                    case "HorizontalSplitPosition": hSplitPos  = v; break;
                    case "VerticalSplitPosition":   vSplitPos  = v; break;
                }
            } catch (NumberFormatException ignored) {}
        }

        // Split mode 2 = freeze; only apply if position is non-zero
        if (hSplitMode == 2 && hSplitPos > 0) sheet.freezeColumns(hSplitPos);
        if (vSplitMode == 2 && vSplitPos > 0) sheet.freezeRows(vSplitPos);
    }

    private Sheet findSheet(String name) {
        for (int i = 0; i < spread.getNumSheets(); i++) {
            Sheet s = spread.getSheet(i);
            if (name.equals(s.getName())) return s;
        }
        return null;
    }
}
