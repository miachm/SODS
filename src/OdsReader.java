package com.github.miachm.SODS;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class OdsReader {
    private static final int TAM_BUFFER = 1000;

    static void load(InputStream in,SpreadSheet spread) throws IOException {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */

        ZipInputStream zip = new ZipInputStream(in);
        byte buff[] = new byte[TAM_BUFFER];
        ZipEntry entry = zip.getNextEntry();

        while (entry != null){
            processEntry(zip,buff,entry,spread);
            entry = zip.getNextEntry();
        }

        zip.close();
    }

    private static void processEntry(ZipInputStream zin, byte[] buff, ZipEntry entry, SpreadSheet spread) {
        System.out.println(entry.getName());

        if (entry.getName().equals("Content.xml")){
            processContent(entry);
        }
    }

    private static void processContent(ZipEntry entry) {
    
    }
}
