package com.github.miachm.sods;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

interface XmlReader extends Closeable{
    XmlReaderInstanceEventImpl load(InputStream in) throws IOException;
}
