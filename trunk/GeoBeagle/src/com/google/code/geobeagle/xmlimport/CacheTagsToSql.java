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
import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import java.io.File;
import java.io.IOException;

@ContextScoped
public class CacheTagsToSql extends CacheTagHandler {

    private String mCacheName = "";
    private final CacheTagSqlWriter mCacheTagSqlWriter;
    private final MessageHandlerInterface mMessageHandler;
    private final ImportWakeLock mWakeLock;
    private String mLastModified;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;

    @Inject
    CacheTagsToSql(CacheTagSqlWriter cacheTagSqlWriter,
            MessageHandlerInterface messageHandler,
            ImportWakeLock importWakeLock,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        mCacheTagSqlWriter = cacheTagSqlWriter;
        mMessageHandler = messageHandler;
        mWakeLock = importWakeLock;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
    }

    @Override
    public void cacheType(String text) {
        mCacheTagSqlWriter.cacheType(text);
    }

    @Override
    public void close(boolean success) {
        mCacheTagSqlWriter.stopWriting(success);
    }

    @Override
    public void container(String text) {
        mCacheTagSqlWriter.container(text);
    }

    @Override
    public void difficulty(String text) {
        mCacheTagSqlWriter.difficulty(text);
    }

    @Override
    public void end() {
        mCacheTagSqlWriter.end();
    }

    @Override
    public void endCache(Source source) throws IOException {
        mMessageHandler.updateName(mCacheName);
        mCacheTagSqlWriter.write(source);
    }

    @Override
    public boolean gpxTime(String gpxTime) {
        return mCacheTagSqlWriter.gpxTime(gpxTime);
    }

    @Override
    public void groundspeakName(String text) {
        mCacheTagSqlWriter.cacheName(text);
    }

    @Override
    public void open(String path) {
        mMessageHandler.updateSource(path);
        mCacheTagSqlWriter.startWriting();
        mCacheTagSqlWriter.gpxName(path);
    }

    @Override
    public void start() {
        new File(mGeoBeagleEnvironment.getDetailsDirectory()).mkdirs();
    }

    @Override
    public void startCache() {
        mCacheName = "";
        mCacheTagSqlWriter.clear();
    }

    @Override
    public void symbol(String text) {
        mCacheTagSqlWriter.symbol(text);
    }

    @Override
    public void terrain(String text) {
        mCacheTagSqlWriter.terrain(text);
    }

    @Override
    public void wpt(String latitude, String longitude) {
        mCacheTagSqlWriter.latitudeLongitude(latitude, longitude);
    }

    @Override
    public void wptDesc(String cacheName) {
        mCacheName = cacheName;
        mCacheTagSqlWriter.cacheName(cacheName);
    }

    @Override
    public void wptName(String wpt) throws IOException {
        mCacheTagSqlWriter.id(wpt);
        mMessageHandler.updateWaypointId(wpt);
        mWakeLock.acquire(GpxLoader.WAKELOCK_DURATION);
    }

    @Override
    public void lastModified(String trimmedText) {
        mLastModified = trimmedText;
    }

    @Override
    public String getLastModified() {
        return mLastModified;
    }

    @Override
    public void archived(String attributeValue) {
        if (attributeValue != null)
            mCacheTagSqlWriter.archived(attributeValue.equalsIgnoreCase("True"));
    }

    @Override
    public void available(String attributeValue) {
        if (attributeValue != null)
            mCacheTagSqlWriter.available(attributeValue.equalsIgnoreCase("True"));
    }

}
