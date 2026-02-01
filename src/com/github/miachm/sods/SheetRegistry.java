package com.github.miachm.sods;

import java.util.IdentityHashMap;
import java.util.Map;

class SheetRegistry {
    private static final Map<Sheet, SpreadSheet> owners = new IdentityHashMap<>();

    static void register(SpreadSheet spread, Sheet sheet) {
        if (spread == null || sheet == null) {
            return;
        }
        owners.put(sheet, spread);
    }

    static void unregister(Sheet sheet) {
        if (sheet == null) {
            return;
        }
        owners.remove(sheet);
    }

    static SpreadSheet lookup(Sheet sheet) {
        if (sheet == null) {
            return null;
        }
        return owners.get(sheet);
    }
}
