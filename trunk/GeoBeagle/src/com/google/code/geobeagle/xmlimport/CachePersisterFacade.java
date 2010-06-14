/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.DetailsDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.LoadDetails;
import com.google.inject.Inject;

import android.os.PowerManager.WakeLock;

import java.io.IOException;

public class CachePersisterFacade implements ICachePersisterFacade {
    private final CacheDetailsWriter mCacheDetailsWriter;
    private String mCacheName = "";
    private final CacheTagSqlWriter mCacheTagWriter;
    private final FileFactory mFileFactory;
    private MessageHandlerInterface mMessageHandler;
    private final WakeLock mWakeLock;
    private String mLastModified;
    private String mDetailsDirectory;

    @Inject
    CachePersisterFacade(CacheTagSqlWriter cacheTagSqlWriter, FileFactory fileFactory,
            @LoadDetails CacheDetailsWriter cacheDetailsWriter, MessageHandlerInterface messageHandler,
            WakeLock wakeLock, @DetailsDirectory String detailsDirectory) {
        mCacheDetailsWriter = cacheDetailsWriter;
        mCacheTagWriter = cacheTagSqlWriter;
        mFileFactory = fileFactory;
        mMessageHandler = messageHandler;
        mWakeLock = wakeLock;
        mDetailsDirectory = detailsDirectory;
    }

    public void cacheType(String text) {
        mCacheTagWriter.cacheType(text);
    }

    public void close(boolean success) {
        mCacheTagWriter.stopWriting(success);
    }

    public void container(String text) {
        mCacheTagWriter.container(text);
    }

    public void difficulty(String text) {
        mCacheTagWriter.difficulty(text);
    }

    public void end() {
        mCacheTagWriter.end();
    }

    public void endCache(Source source) throws IOException {
        mMessageHandler.updateName(mCacheName);
        mCacheDetailsWriter.close();
        mCacheTagWriter.write(source);
    }

    public boolean gpxTime(String gpxTime) {
        return mCacheTagWriter.gpxTime(gpxTime);
    }

    public void groundspeakName(String text) {
        mCacheTagWriter.cacheName(text);
    }

    public void hint(String text) throws IOException {
        mCacheDetailsWriter.writeHint(text);
    }

    public void line(String text) throws IOException {
        mCacheDetailsWriter.writeLine(text);
    }

    public void logDate(String text) throws IOException {
        mCacheDetailsWriter.writeLogDate(text);
    }

    public void open(String path) {
        mMessageHandler.updateSource(path);
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName(path);
        mCacheDetailsWriter.gpxName(path);
    }

    public void start() {
        mFileFactory.createFile(mDetailsDirectory).mkdirs();
    }

    public void startCache() {
        mCacheName = "";
        mCacheTagWriter.clear();
    }

    public void symbol(String text) {
        mCacheTagWriter.symbol(text);
    }

    public void terrain(String text) {
        mCacheTagWriter.terrain(text);
    }

    public void wpt(String latitude, String longitude) {
        mCacheTagWriter.latitudeLongitude(latitude, longitude);
        mCacheDetailsWriter.latitudeLongitude(latitude, longitude);
    }

    public void wptDesc(String cacheName) {
        mCacheName = cacheName;
        mCacheTagWriter.cacheName(cacheName);
    }

    public void wptName(String wpt) throws IOException {
        mCacheDetailsWriter.open(wpt);
        mCacheDetailsWriter.writeWptName(wpt);
        mCacheTagWriter.id(wpt);
        mMessageHandler.updateWaypointId(wpt);
        mWakeLock.acquire(GpxLoader.WAKELOCK_DURATION);
    }

    public void lastModified(String trimmedText) {
        mLastModified = trimmedText;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public void archived(String attributeValue) {
        if (attributeValue != null)
            mCacheTagWriter.archived(attributeValue.equalsIgnoreCase("True"));
    }

    public void available(String attributeValue) {
        if (attributeValue != null)
            mCacheTagWriter.available(attributeValue.equalsIgnoreCase("True"));
    }

    public double getLatitude() {
        return mCacheTagWriter.getLatitude();
    }

    public double getLongitude() {
        return mCacheTagWriter.getLongitude();
    }

}
