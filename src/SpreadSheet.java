package com.github.miachm.SODS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpreadSheet implements Cloneable {

    private List<Sheet> sheets = new ArrayList<Sheet>();

    public SpreadSheet() {
        sheets.add(new Sheet());
        //TODO
    }

    public SpreadSheet(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public SpreadSheet(InputStream in){
        // TODO
    }

    @Override
    public Object clone()
    {
        // TODO Implement
        return new SpreadSheet();
    }

    public List<Sheet> getSheets()
    {
        return Collections.unmodifiableList(sheets);
    }

    public void appendSheet()
    {
        addSheet(new Sheet());
    }

    public void addSheet(Sheet sheet)
    {
        // TODO
    }
}