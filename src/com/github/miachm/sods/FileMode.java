package com.github.miachm.sods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class FileMode implements UncompressorMode {
    private final ZipFile zipFile;
    private final Enumeration<? extends ZipEntry> entries;
    private ZipEntry currentEntry;

    FileMode(File file) throws IOException {
        this.zipFile = new ZipFile(file);
        this.entries = zipFile.entries();
    }

    @Override
    public String nextFile() {
        if (entries.hasMoreElements()) {
            currentEntry = entries.nextElement();
            return currentEntry.getName();
        } else {
            currentEntry = null;
            return null;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (currentEntry == null) {
            throw new IllegalStateException("No current entry available");
        }
        return zipFile.getInputStream(currentEntry);
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
