package com.github.miachm.sods;

class FileEntry {
    String path;
    String mimetype;
    byte[] data;
    
    FileEntry(String path, String mimetype, byte[] data)
    {
        this.path = path;
        this.mimetype = mimetype;
        this.data = data;
    }
}
