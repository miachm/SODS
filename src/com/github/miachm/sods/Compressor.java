package com.github.miachm.sods;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
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
        addEntry(data,name,false);
    }

    void addEntry(byte[] data,String name, boolean store) throws IOException {
        ZipEntry zipEntry = new ZipEntry(name);
        if (store) {
            CRC32 checksum = new CRC32();
            checksum.update(data);
            zipEntry.setCrc(checksum.getValue());
            zipEntry.setSize(data.length);
            zipEntry.setMethod(ZipEntry.STORED);
        }
        out.putNextEntry(zipEntry);
        out.write(data);
        out.closeEntry();
    }
}
