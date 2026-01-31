package com.github.miachm.sods;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

interface UncompressorMode extends Closeable {
    String nextFile() throws IOException;
    InputStream getInputStream() throws IOException;
}
