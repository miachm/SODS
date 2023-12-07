package com.github.miachm.sods;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Compressor implements Closeable {
    private final ZipOutputStream out;

    Compressor(OutputStream o){
        this.out = new ZipOutputStream(o);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void flush() throws IOException {
        out.flush();
    }

    void addEntry(byte[] data,String name) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(data);
        out.closeEntry();
    }
}
