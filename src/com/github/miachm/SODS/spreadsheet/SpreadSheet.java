package com.github.miachm.SODS.spreadsheet;

import com.github.miachm.SODS.io.OdsReader;
import com.github.miachm.SODS.io.OdsWritter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SpreadSheet implements Cloneable {

    private List<Sheet> sheets = new ArrayList<Sheet>();

    public SpreadSheet() {
    }

    public SpreadSheet(File file) throws IOException {
        this(new FileInputStream(file));
    }

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

    public SpreadSheet(InputStream in) throws IOException {
        OdsReader.load(in,this);
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

    public void appendSheet(Sheet sheet)
    {
        addSheet(sheet,sheets.size());
    }

    public void addSheet(Sheet sheet,int pos) {
        sheets.add(pos,sheet);
    }

    public void clear(){
        sheets.clear();
    }

    public void deleteSheet(int pos) {
        sheets.remove(pos);
    }

    public boolean deleteSheet(String name){
        return sheets.removeIf((sheet) -> sheet.getName().equals(name));
    }

    public boolean deleteSheet(Sheet sheet){
        return deleteSheet(sheet.getName());
    }

    public List<Sheet> getSheets()
    {
        return Collections.unmodifiableList(sheets);
    }

    public int getNumSheets(){
        return sheets.size();
    }

    public Sheet getSheet(String name)
    {
        for (Sheet sheet : sheets)
            if (sheet.getName().equals(name))
                return sheet;

        return null;
    }

    public Sheet getSheet(int index) {
        return sheets.get(index);
    }

    public void setSheet(Sheet sheet,int pos){
        sheets.set(pos,sheet);
    }

    public void save(File out) throws IOException {
        save(new FileOutputStream(out));
    }

    public void save(OutputStream out) throws IOException {
        OdsWritter.save(out,this);
    }

    public void sortSheets(){
        Collections.sort(sheets);
    }

    public void sortSheets(Comparator<Sheet> comparator){
        sheets.sort(comparator);
    }
}