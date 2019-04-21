package com.github.miachm.sods;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Uncompressor implements Closeable{

    private static final int TAM_BUFFER = 1000;
    private final ZipInputStream zip;

    Uncompressor(InputStream in){
        this.zip = new ZipInputStream(in);
    }

    String nextFile() throws IOException {
        ZipEntry entry = zip.getNextEntry();
        if (entry != null)
            return entry.getName();
        else
            return null;
    }

    InputStream getInputStream()
    {
        return new UncompressorInputStream(zip);
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
