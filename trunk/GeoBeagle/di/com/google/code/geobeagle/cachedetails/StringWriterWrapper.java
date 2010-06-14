
package com.google.code.geobeagle.cachedetails;

import java.io.IOException;
import java.io.StringWriter;

public class StringWriterWrapper implements com.google.code.geobeagle.cachedetails.Writer {

    private final StringWriter stringWriter;

    public StringWriterWrapper() {
        this.stringWriter = new StringWriter();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void open(String path) throws IOException {
    }

    @Override
    public void write(String str) throws IOException {
        stringWriter.write(str);
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }
}
