
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;

import java.io.IOException;

public interface ICachePersisterFacade {

    public abstract void cacheType(String text);

    public abstract void close(boolean success);

    public abstract void container(String text);

    public abstract void difficulty(String text);

    public abstract void end();

    public abstract void endCache(Source source) throws IOException;

    public abstract boolean gpxTime(String gpxTime);

    public abstract void groundspeakName(String text);

    public abstract void hint(String text) throws IOException;

    public abstract void line(String text) throws IOException;

    public abstract void logDate(String text) throws IOException;

    public abstract void open(String path);

    public abstract void start();

    public abstract void startCache();

    public abstract void symbol(String text);

    public abstract void terrain(String text);

    public abstract void wpt(String latitude, String longitude);

    public abstract void wptDesc(String cacheName);

    public abstract void wptName(String wpt) throws IOException;

    public abstract void lastModified(String trimmedText);

    public abstract String getLastModified();

    public abstract void archived(String attributeValue);

    public abstract void available(String attributeValue);

    public abstract double getLatitude();

    public abstract double getLongitude();

}
