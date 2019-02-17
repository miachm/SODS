package com.github.miachm.sods.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Uncompressor {

    private static final int TAM_BUFFER = 1000;
    private byte buff[] = new byte[TAM_BUFFER];
    private Map<String,byte[]> map = new HashMap<String,byte[]>();
    private ZipInputStream zip;

    Uncompressor(InputStream in){
        this.zip = new ZipInputStream(in);
    }

    Map<String,byte[]> getFiles() throws IOException {
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            if (!entry.isDirectory()) {
                processEntry(entry);
                zip.closeEntry();
            }
            entry = zip.getNextEntry();
        }

        zip.close();
        return map;
    }

    private void processEntry(ZipEntry entry) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int len;

        while (true){
            len = zip.read(buff);
            if (len == -1) {
                stream.close();
                map.put(entry.getName(),stream.toByteArray());
                return;
            }
            stream.write(buff,0,len);
        }
    }
}
