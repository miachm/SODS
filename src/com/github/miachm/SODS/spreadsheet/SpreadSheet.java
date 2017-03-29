package com.github.miachm.SODS.com.github.miachm.SODS.spreadsheet;

import com.github.miachm.SODS.com.github.miachm.SODS.input.OdsReader;

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

    public void deleteSheet(String name){
        sheets.removeIf((sheet) -> sheet.getName().equals(name));
    }

    public void deleteSheet(Sheet sheet){
        deleteSheet(sheet.getName());
    }

    public List<Sheet> getSheets()
    {
        return Collections.unmodifiableList(sheets);
    }

    public int getNumSheets(){
        return sheets.size();
    }

    public void setSheet(Sheet sheet,int pos){
        sheets.set(pos,sheet);
    }

    public void save(File out) throws FileNotFoundException {
        save(new FileOutputStream(out));
    }

    public void save(OutputStream out){
        // TODO
    }

    public void sortSheets(){
        sortSheets((a,b) -> a.getName().compareTo(b.getName()));
    }

    public void sortSheets(Comparator<Sheet> comparator){
        sheets.sort(comparator);
    }
}