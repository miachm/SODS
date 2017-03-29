package com.github.miachm.SODS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class OdsReader {
    private static final int TAM_BUFFER = 1000;

    static void load(InputStream in,SpreadSheet spread) throws IOException {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */

        Map<String,byte[]> files = uncompress(in);
    }

    private static Map<String,byte[]> uncompress(InputStream in) throws IOException {
        Map<String,byte[]> map = new HashMap<String,byte[]>();
        ZipInputStream zip = new ZipInputStream(in);
        byte buff[] = new byte[TAM_BUFFER];

        ZipEntry entry = zip.getNextEntry();
        while (entry != null){
            if (!entry.isDirectory()) {
                processEntry(map, zip, buff, entry);
                zip.closeEntry();
            }
            entry = zip.getNextEntry();
        }

        zip.close();

        return map;
    }

    private static void processEntry(Map<String,byte[]> map,ZipInputStream zin, byte[] buff,ZipEntry entry) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int len;

        while (true){
            len = zin.read(buff);
            if (len == -1) {
                stream.close();
                map.put(entry.getName(),stream.toByteArray());
                return;
            }
            stream.write(buff,0,len);
        }
    }
}
