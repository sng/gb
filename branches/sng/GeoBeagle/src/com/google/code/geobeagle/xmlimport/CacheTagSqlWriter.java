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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheWriter;

/**
 * @author sng
 */
public class CacheTagSqlWriter {
    private final CacheTypeFactory mCacheTypeFactory;
    
    private CacheType mCacheType;
    private final CacheWriter mCacheWriter;
    private int mContainer;
    private int mDifficulty;
    private boolean mFound;
    private String mGpxName;
    private CharSequence mId;
    private double mLatitude;
    private double mLongitude;
    private CharSequence mName;
    private String mSqlDate;

    private int mTerrain;

    public CacheTagSqlWriter(CacheWriter cacheWriter, 
            CacheTypeFactory cacheTypeFactory) {
        mCacheWriter = cacheWriter;
        mCacheTypeFactory = cacheTypeFactory;
    }

    public void cacheName(String name) {
        mName = name;
    }

    public void cacheType(String type) {
        mCacheType = mCacheTypeFactory.fromTag(type);
    }

    public void clear() { // TODO: ensure source is not reset
        mId = mName = null;
        mLatitude = mLongitude = 0;
        mFound = false;
        mCacheType = CacheType.NULL;
        mDifficulty = 0;
        mTerrain = 0;
    }

    public void container(String container) {
        mContainer = mCacheTypeFactory.container(container);
    }

    public void difficulty(String difficulty) {
        mDifficulty = mCacheTypeFactory.stars(difficulty);
    }

    public void end() {
        mCacheWriter.clearEarlierLoads();
    }

    public void gpxName(String gpxName) {
        mGpxName = gpxName;
    }

    /**
     * @param gpxTime
     * @return true if we should load this gpx; false if the gpx is already
     *         loaded.
     */
    public boolean gpxTime(String gpxTime) {
        mSqlDate = isoTimeToSql(gpxTime);
        if (mCacheWriter.isGpxAlreadyLoaded(mGpxName, mSqlDate)) {
            return false;
        }
        mCacheWriter.clearCaches(mGpxName);
        return true;
    }

    public void id(CharSequence id) {
        mId = id;
    }

    public String isoTimeToSql(String gpxTime) {
        return gpxTime.substring(0, 10) + " " + gpxTime.substring(11, 19);
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = Double.parseDouble(latitude);
        mLongitude = Double.parseDouble(longitude);
    }

    public void startWriting() {
        mSqlDate = "2000-01-01T12:00:00";
        mCacheWriter.startWriting();
    }

    public void stopWriting(boolean successfulGpxImport) {
        mCacheWriter.stopWriting();
        if (successfulGpxImport)
            mCacheWriter.writeGpx(mGpxName, mSqlDate);
    }

    public void symbol(String symbol) {
        mFound = symbol.equals("Geocache Found");
    }

    public void terrain(String terrain) {
        mTerrain = mCacheTypeFactory.stars(terrain);
    }

    public void write(Source source) {
        if (!mFound)
            mCacheWriter.insertAndUpdateCache(mId, mName, mLatitude, mLongitude, source, mGpxName,
                    mCacheType, mDifficulty, mTerrain, mContainer);
    }
}
