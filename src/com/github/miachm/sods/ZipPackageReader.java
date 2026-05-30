package com.github.miachm.sods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads all entries from an ODF ZIP package into memory.
 */
class ZipPackageReader {

    static Map<String, byte[]> readAll(InputStream in) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        Uncompressor uncompressor = new Uncompressor(in);
        try {
            String name;
            while ((name = uncompressor.nextFile()) != null) {
                entries.put(name, readStream(uncompressor.getInputStream()));
            }
        } finally {
            uncompressor.close();
        }
        return entries;
    }

    static Map<String, byte[]> readAll(File file) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        Uncompressor uncompressor = new Uncompressor(file);
        try {
            String name;
            while ((name = uncompressor.nextFile()) != null) {
                entries.put(name, readStream(uncompressor.getInputStream()));
            }
        } finally {
            uncompressor.close();
        }
        return entries;
    }

    private static byte[] readStream(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) >= 0) {
            if (read == 0) continue;
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }
}
