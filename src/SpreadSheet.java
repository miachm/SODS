package com.github.miachm.SODS;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SpreadSheet implements Cloneable {

    private List<Sheet> sheets = new ArrayList<Sheet>();

    public SpreadSheet() {
    }

    public SpreadSheet(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public SpreadSheet(InputStream in){
        // TODO
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
        // TODO
    }

    public void deleteSheet(String name){
        // TODO
    }

    public void deleteSheet(Sheet sheet){
        // TODO
    }

    public List<Sheet> getSheets()
    {
        return Collections.unmodifiableList(sheets);
    }

    public int getNumSheets(){
        return sheets.size();
    }

    public void setSheet(int pos){
        // TODO
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
        // TODO
    }
}