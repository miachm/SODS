package com.github.miachm.sods;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class Uncompressor implements Closeable {
    private final UncompressorMode mode;

    Uncompressor(InputStream in) {
        this.mode = new StreamMode(in);
    }

    Uncompressor(File file) throws IOException {
        this.mode = new FileMode(file);
    }

    String nextFile() throws IOException {
        return mode.nextFile();
    }

    InputStream getInputStream() throws IOException {
        return mode.getInputStream();
    }

    @Override
    public void close() throws IOException {
        mode.close();
    }
}
