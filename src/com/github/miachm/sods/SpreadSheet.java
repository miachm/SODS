package com.github.miachm.sods;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spreadsheet is the base class for handle a Spreadsheet.
 *
 * You can create an empty Spreadsheet or load an existing one.
 */

public class SpreadSheet implements Cloneable {

    private final List<Sheet> sheets = new ArrayList<Sheet>();
    private final Map<String, FileEntry> extraFiles = new HashMap<>();
    private final Map<String, List<SheetImage>> pendingImages = new HashMap<>();
    private int imageCounter = 1;

    /**
     * Create an empty spreadsheet
     */

    public SpreadSheet()
    {
    }

    /**
     * Load a Spreadsheet from an ODS file.
     *
     * @param file The file to load. It must be a valid readable file
     * @throws NullPointerException If the file is null
     * @throws FileNotFoundException If the file doesn't exist or it can be readed
     * @throws NotAnOdsException If the file isn't an ODS file.
     * @throws OperationNotSupportedException If the ODS file has a feature which it's not implemented in this library
     * @throws IOException If an unexpected IO error is produced
     * @see #SpreadSheet(InputStream)
     */
    public SpreadSheet(File file) throws IOException {
        OdsReader.load(file, this);
    }

   /**
     * Load a Spreadsheet from an ODS file specifying options.
     *
     * @param file The file to load. It must be a valid readable file
     * @param options The options to use when loading the spreadsheet
     * @throws NullPointerException If the file is null
     * @throws FileNotFoundException If the file doesn't exist or it can be readed
     * @throws NotAnOdsException If the file isn't an ODS file.
     * @throws OperationNotSupportedException If the ODS file has a feature which it's not implemented in this library
     * @throws IOException If an unexpected IO error is produced
     * @see #SpreadSheet(InputStream)
     */
    public SpreadSheet(File file, OdsOptionParameters options) throws IOException {
        OdsReader.load(file, this, options);
    }

    /**
     * Load a Spreadsheet from an inputstream.
     * @param in The inputstream to read
     * @throws NullPointerException If the inputstream is null
     * @throws NotAnOdsException If the file isn't an ODS file.
     * @throws OperationNotSupportedException If the ODS file has a feature which it's not implemented in this library
     * @throws IOException If an unexpected IO error is produced
     * @see #SpreadSheet(InputStream)
     */
    public SpreadSheet(InputStream in) throws IOException {
        OdsReader.load(in,this);
    }

    public SpreadSheet(InputStream in, OdsOptionParameters options) throws IOException {
        if (options == null) {
            throw new NullPointerException("OdsOptionParameters cannot be null");
        }
        OdsReader.load(in, this, options);
    }
    /**
     * Append a new sheet at the end of the book
     *
     * @param sheet A valid not-null sheet
     * @throws NullPointerException if the sheet is null
     */
    public void appendSheet(Sheet sheet)
    {
        addSheet(sheet,sheets.size());
    }

    /**
     * Add a new sheet in a specific position
     * @param sheet A valid not-null sheet.
     * @param pos Position where insert. It must be in the range [0, getNumSheets()]
     * @throws NullPointerException if the sheet is null
     * @throws IndexOutOfBoundsException If the position is out of range
     */
    public void addSheet(Sheet sheet,int pos) {
        if (sheet == null)
            throw new NullPointerException();

        sheets.add(pos,sheet);
        sheet.setParent(this);
    }

    /**
     * Remove all sheets of the book. This only remove the link, the sheets objects are not modified in any way.
     */
    public void clear(){
        sheets.clear();
        pendingImages.clear();
    }

    /**
     * Remove a specific sheet from the book
     *
     * @param pos The index of the sheet
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void deleteSheet(int pos) {
        sheets.remove(pos);
    }

    /**
     * Remove a specific sheet from the book specified by the name.
     *
     * @param name The name of the sheet.
     * @return True if the sheet was removed, false otherwise
     * @see #deleteSheet(Sheet)
     */
    public boolean deleteSheet(String name){
        return sheets.removeIf((sheet) -> sheet.getName().equals(name));
    }

    /**
     * Remove the specified sheet of the book.
     * @param sheet sheet to remove.
     * @return True if the sheet was removed.
     * @see #deleteSheet(String)
     */
    public boolean deleteSheet(Sheet sheet){
        return sheets.remove(sheet);
    }

    /**
     * Return all the sheets of the book in a list.
     *
     * @return An unmodifiable sheets list.
     */
    public List<Sheet> getSheets()
    {
        return Collections.unmodifiableList(sheets);
    }

    Collection<FileEntry> getExtraFiles() {
        return extraFiles.values();
    }

    FileEntry getExtraFile(String path) {
        return extraFiles.get(path);
    }

    void registerFile(String path, String mimeType, byte[] data) {
        if (path == null || data == null) {
            return;
        }
        extraFiles.put(path, new FileEntry(path, mimeType, data));
        List<SheetImage> pending = pendingImages.remove(path);
        if (pending != null) {
            for (SheetImage image : pending) {
                if (image == null) continue;
                image.setData(data);
                if (image.getMimeType() == null) {
                    image.setMimeType(mimeType);
                }
                if (image.getPath() == null) {
                    image.setPath(path);
                }
            }
        }
    }

    void registerImagePath(String path, SheetImage image) {
        if (path == null || image == null) {
            return;
        }
        FileEntry entry = extraFiles.get(path);
        if (entry != null) {
            image.setData(entry.data);
            if (image.getMimeType() == null) {
                image.setMimeType(entry.mimetype);
            }
            if (image.getPath() == null) {
                image.setPath(path);
            }
            return;
        }
        List<SheetImage> list = pendingImages.computeIfAbsent(path, key -> new ArrayList<>());
        if (!list.contains(image)) {
            list.add(image);
        }
    }

    void registerImage(SheetImage image) {
        if (image == null) {
            return;
        }
        String path = image.getPath();
        if (path == null || path.trim().isEmpty()) {
            String extension = extensionForMime(image.getMimeType());
            path = buildImagePath(extension);
            image.setPath(path);
        }
        if (image.getMimeType() == null) {
            image.setMimeType(mimeTypeForExtension(path));
        }
        byte[] data = image.getDataInternal();
        if (data != null) {
            extraFiles.put(path, new FileEntry(path, image.getMimeType(), data));
        }
    }

    private String buildImagePath(String extension) {
        String ext = (extension == null || extension.isEmpty()) ? "png" : extension;
        String path;
        do {
            path = "Pictures/Image" + imageCounter++ + "." + ext;
        } while (extraFiles.containsKey(path));
        return path;
    }

    private String extensionForMime(String mimeType) {
        if (mimeType == null) return "png";
        String normalized = mimeType.toLowerCase(Locale.US);
        if (normalized.contains("png")) return "png";
        if (normalized.contains("jpeg") || normalized.contains("jpg")) return "jpg";
        if (normalized.contains("gif")) return "gif";
        if (normalized.contains("bmp")) return "bmp";
        if (normalized.contains("svg")) return "svg";
        return "png";
    }

    private String mimeTypeForExtension(String path) {
        if (path == null) return null;
        String lower = path.toLowerCase(Locale.US);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return null;
    }

    /**
     * Return the number of sheets in the book
     *
     * @return The number of sheets in the book
     */
    public int getNumSheets(){
        return sheets.size();
    }

    /**
     * Return a sheet with a given name. If the sheet doesn't exist will return null
     *
     * @param name The name to look up.
     * @return The sheet with a given name, if it doesn't exist return null
     */
    public Sheet getSheet(String name)
    {
        for (Sheet sheet : sheets)
            if (sheet.getName().equals(name))
                return sheet;

        return null;
    }

    /**
     * Return a sheet with a given index.
     *
     * @param index Position of the sheet
     * @return The sheet
     * @throws IndexOutOfBoundsException If the position is invalid.
     */
    public Sheet getSheet(int index) {
        return sheets.get(index);
    }

    /**
     * Replace the sheet in the position pos.
     *
     * @param sheet The new sheet, it must be not-null.
     * @param pos The position where insert the sheet
     * @throws NullPointerException if the sheet is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public void setSheet(Sheet sheet, int pos)
    {
        if (sheet == null)
            throw new NullPointerException();
        sheets.set(pos,sheet);
        sheet.setParent(this);
    }

    /**
     * Save this SpreadSheet in a ODS file.
     *
     * @param out The file to be writted. It must be no-null and be in a valid path
     * @throws NullPointerException If the file is null
     * @throws FileNotFoundException If the file is an invalid path
     * @throws IOException In case of an io error.
     */
    public void save(File out) throws IOException {
        save(new FileOutputStream(out));
    }

    /**
     * Save this Spreadsheet to the stream in the ODS format
     *
     * @param out The outputstream to be writted. It must be no-null
     * @throws NullPointerException If the OutputStream is null
     * @throws IOException In case of an io error.
     */
    public void save(OutputStream out) throws IOException {
        OdsWritter.save(out,this);
    }

    /**
     * Sort the sheets by name
     *
     * @deprecated This operation will be discarded for simplicity. You can easily recreate it with client code
     */
    @Deprecated
    public void sortSheets(){
        Collections.sort(sheets);
    }

    /**
     * Sort the sheets by a custom comparator
     *
     * @param comparator The comparator used in the sorting
     * @deprecated This operation will be discarded for simplicity. You can easily recreate it with client code
     */
    @Deprecated
    public void sortSheets(Comparator<Sheet> comparator){
        sheets.sort(comparator);
    }

    /** Trim the sheets to the minimum dimensions possible
     * This method is equivalent to call sheet.trim() to each sheet of the spreadsheet
     * 
     * @deprecated this operation relay in sheet.trim(), which is also deprecated
     */
    public void trimSheets()
    {
        for (Sheet sheet : sheets)
            sheet.trim();
    }
    /**
     * Compare two spreadsheets. Two spreadsheets are equals if they have the same sheets and in the same order
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpreadSheet that = (SpreadSheet) o;

        return sheets.equals(that.sheets);
    }

    @Override
    public int hashCode() {
        return sheets.hashCode();
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public String toString() {
        return "SpreadSheet{" +
                "sheets=" + sheets +
                '}';
    }
}
