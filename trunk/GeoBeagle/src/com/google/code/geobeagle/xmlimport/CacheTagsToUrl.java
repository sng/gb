
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.inject.Inject;

import java.io.IOException;

public class CacheTagsToUrl implements ICachePersisterFacade {

    private final StringWriterWrapper stringWriterWrapper;

    @Inject
    CacheTagsToUrl(StringWriterWrapper stringWriterWrapper) {
        this.stringWriterWrapper = stringWriterWrapper;
    }

    @Override
    public void archived(String attributeValue) {
    }

    @Override
    public void available(String attributeValue) {
    }

    @Override
    public void cacheType(String text) throws IOException {
    }

    @Override
    public void close(boolean success) {
    }

    @Override
    public void container(String text) throws IOException {
    }

    @Override
    public void difficulty(String text) throws IOException {
    }

    @Override
    public void end() {
    }

    @Override
    public void endCache(Source source) throws IOException {
    }

    @Override
    public String getLastModified() {
        return null;
    }

    @Override
    public boolean gpxTime(String gpxTime) {
        return false;
    }

    @Override
    public void groundspeakName(String text) throws IOException {
    }

    @Override
    public void hint(String text) throws IOException {
    }

    @Override
    public void lastModified(String trimmedText) {
    }

    @Override
    public void line(String text) throws IOException {
    }

    @Override
    public void logDate(String text) throws IOException {
    }

    @Override
    public void logText(String trimmedText) throws IOException {
    }

    @Override
    public void logType(String trimmedText) throws IOException {
    }

    @Override
    public void longDescription(String trimmedText) throws IOException {
    }

    @Override
    public void open(String path) throws IOException {
    }

    @Override
    public void placedBy(String trimmedText) throws IOException {
    }

    @Override
    public void setEncrypted(boolean mLogEncrypted) {
    }

    @Override
    public void shortDescription(String trimmedText) throws IOException {
    }

    @Override
    public void start() {
    }

    @Override
    public void startCache() {
    }

    @Override
    public void symbol(String text) {
    }

    @Override
    public void terrain(String text) throws IOException {
    }

    @Override
    public void wpt(String latitude, String longitude) {
    }

    @Override
    public void wptDesc(String cacheName) {
    }

    @Override
    public void wptName(String wpt) throws IOException {
    }

    @Override
    public void wptTime(String trimmedText) throws IOException {
    }

    @Override
    public void logFinder(String text) {
    }

    @Override
    public void url(String text) {
        try {
            stringWriterWrapper.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
