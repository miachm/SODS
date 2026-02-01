package com.github.miachm.sods;

class ChartRangeResolver {
    static Range resolveRange(String rangeAddress, Sheet chartSheet) {
        if (rangeAddress == null) {
            return null;
        }
        String trimmed = rangeAddress.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] spaceParts = trimmed.split("\\s+");
        String address = spaceParts[0];

        String[] rangeParts = address.split(":");
        String start = rangeParts[0];
        String end = rangeParts.length > 1 ? rangeParts[1] : rangeParts[0];

        RangeAddressHelper.SheetPart startPart = RangeAddressHelper.splitSheetPart(start);
        RangeAddressHelper.SheetPart endPart = RangeAddressHelper.splitSheetPart(end);
        String sheetName = startPart.sheetName != null ? startPart.sheetName : endPart.sheetName;
        if (sheetName == null) {
            if (chartSheet == null) {
                throw new IndexOutOfBoundsException("Invalid chart range: " + rangeAddress);
            }
            sheetName = chartSheet.getName();
        } else {
            sheetName = RangeAddressHelper.unquoteSheetName(sheetName);
        }

        String a1Start = RangeAddressHelper.sanitizeA1(startPart.cellRef);
        String a1End = RangeAddressHelper.sanitizeA1(endPart.cellRef);
        String a1Notation = rangeParts.length > 1 ? a1Start + ":" + a1End : a1Start;
        if (a1Start.isEmpty()) {
            throw new IndexOutOfBoundsException("Invalid chart range: " + rangeAddress);
        }

        Sheet targetSheet = resolveSheet(chartSheet, sheetName);
        if (targetSheet == null) {
            throw new IndexOutOfBoundsException("Invalid chart range: " + rangeAddress);
        }
        return targetSheet.getRange(a1Notation);
    }

    private static Sheet resolveSheet(Sheet chartSheet, String sheetName) {
        if (chartSheet != null && sheetName.equals(chartSheet.getName())) {
            return chartSheet;
        }
        if (chartSheet != null) {
            SpreadSheet parent = SheetRegistry.lookup(chartSheet);
            if (parent != null) {
                return parent.getSheet(sheetName);
            }
        }
        return null;
    }

}
