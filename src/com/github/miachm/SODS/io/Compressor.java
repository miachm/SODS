package com.github.miachm.SODS.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by MiguelPC on 23/04/2017.
 */
public class Compressor implements Closeable {
    private ZipOutputStream out;

    public Compressor(OutputStream o){
        this.out = new ZipOutputStream(o);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void addEntry(byte[] data,String name) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(data);
        out.closeEntry();
    }
}
