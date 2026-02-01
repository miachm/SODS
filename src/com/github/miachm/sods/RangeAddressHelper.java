package com.github.miachm.sods;

class RangeAddressHelper {
    static SheetPart splitSheetPart(String part) {
        if (part == null) return new SheetPart(null, "");
        int dotIndex = part.lastIndexOf('.');
        if (dotIndex < 0) {
            return new SheetPart(null, part);
        }
        String sheetName = part.substring(0, dotIndex);
        String cellRef = part.substring(dotIndex + 1);
        return new SheetPart(sheetName, cellRef);
    }

    static String sanitizeA1(String cellRef) {
        if (cellRef == null) return "";
        return cellRef.replace("$", "");
    }

    static String unquoteSheetName(String sheetName) {
        if (sheetName == null) return null;
        String trimmed = sheetName.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("'") && trimmed.endsWith("'")) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            return inner.replace("''", "'");
        }
        return trimmed;
    }

    static class SheetPart {
        final String sheetName;
        final String cellRef;

        SheetPart(String sheetName, String cellRef) {
            this.sheetName = sheetName;
            this.cellRef = cellRef;
        }
    }
}
