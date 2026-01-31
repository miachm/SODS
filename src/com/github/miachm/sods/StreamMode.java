package com.github.miachm.sods;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class StreamMode implements UncompressorMode {
    private final ZipInputStream zip;

    StreamMode(InputStream in) {
        this.zip = new ZipInputStream(in);
    }

    @Override
    public String nextFile() throws IOException {
        ZipEntry entry = zip.getNextEntry();
        if (entry != null)
            return entry.getName();
        else
            return null;
    }

    @Override
    public InputStream getInputStream() {
        return new UncompressorInputStream(zip);
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
