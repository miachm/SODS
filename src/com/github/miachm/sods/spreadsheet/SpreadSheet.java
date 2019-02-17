package com.github.miachm.sods.spreadsheet;

import com.github.miachm.sods.io.OdsReader;
import com.github.miachm.sods.io.OdsWritter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Spreadsheet is the base class for handle a Spreadsheet.
 *
 * You can create an empty Spreadsheet or load an existing one.
 */

public class SpreadSheet implements Cloneable {

    private List<Sheet> sheets = new ArrayList<Sheet>();

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
     * @throws com.github.miachm.sods.exceptions.NotAnOds If the file isn't an ODS file.
     * @throws com.github.miachm.sods.exceptions.OperationNotSupported If the ODS file has a feature which it's not implemented in this library
     * @throws IOException If an unexpected IO error is produced
     * @see SpreadSheet(InputStream)
     */
    public SpreadSheet(File file) throws IOException {
        this(new FileInputStream(file));
    }

    /**
     * Load a Spreadsheet from an inputstream.
     * @param in The inputstream to read
     * @throws NullPointerException If the inputstream is null
     * @throws com.github.miachm.sods.exceptions.NotAnOds If the file isn't an ODS file.
     * @throws com.github.miachm.sods.exceptions.OperationNotSupported If the ODS file has a feature which it's not implemented in this library
     * @throws IOException If an unexpected IO error is produced
     * @see SpreadSheet(InputStream)
     */
    public SpreadSheet(InputStream in) throws IOException {
        OdsReader.load(in,this);
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
    }

    /**
     * Remove all sheets of the book. This only remove the link, the sheets objects are not modified in any way.
     */
    public void clear(){
        sheets.clear();
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
     * @see deleteSheet(Sheet)
     */
    public boolean deleteSheet(String name){
        return sheets.removeIf((sheet) -> sheet.getName().equals(name));
    }

    /**
     * Remove the specified sheet of the book.
     * @param sheet sheet to remove.
     * @return True if the sheet was removed.
     * @see deleteSheet(String)
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
     * @throws NullPointerException if the sheet is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public void setSheet(Sheet sheet, int pos)
    {
        if (sheet == null)
            throw new NullPointerException();
        sheets.set(pos,sheet);
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
     */
    public void sortSheets(){
        Collections.sort(sheets);
    }

    /**
     * Sort the sheets by a custom comparator
     */
    public void sortSheets(Comparator<Sheet> comparator){
        sheets.sort(comparator);
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
        SpreadSheet spread = new SpreadSheet();

        for (Sheet sheet : sheets){
            spread.appendSheet((Sheet) sheet.clone());
        }

        return spread;
    }

    @Override
    public String toString() {
        return "SpreadSheet{" +
                "sheets=" + sheets +
                '}';
    }
}