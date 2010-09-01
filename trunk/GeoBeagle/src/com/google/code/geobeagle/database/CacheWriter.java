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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.SharedPreferences;

/**
 * @author sng
 */
public class CacheWriter {
    public static final String SQLS_CLEAR_EARLIER_LOADS[] = {
            Database.SQL_DELETE_OLD_CACHES, Database.SQL_DELETE_OLD_GPX,
            Database.SQL_RESET_DELETE_ME_CACHES, Database.SQL_RESET_DELETE_ME_GPX
    };
    private final DbToGeocacheAdapter mDbToGeocacheAdapter;
    private final Provider<ISQLiteDatabase> sqliteProvider;
    private final SharedPreferences mSharedPreferences;

    @Inject
    CacheWriter(Provider<ISQLiteDatabase> writableDatabaseProvider,
            DbToGeocacheAdapter dbToGeocacheAdapter,
            SharedPreferences sharedPreferences) {
        sqliteProvider = writableDatabaseProvider;
        mDbToGeocacheAdapter = dbToGeocacheAdapter;
        mSharedPreferences = sharedPreferences;
    }


    public void deleteCache(CharSequence id) {
        sqliteProvider.get().execSQL(Database.SQL_DELETE_CACHE, id);
    }

    public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
            double longitude, Source sourceType, String sourceName, CacheType cacheType,
            int difficulty,
            int terrain,
            int container,
            boolean available,
            boolean archived,
            boolean mFound) {
        boolean showFoundCaches = mSharedPreferences.getBoolean(EditPreferences.SHOW_FOUND_CACHES,
                false);
        boolean visible = showFoundCaches || !mFound;
        sqliteProvider.get().execSQL(Database.SQL_REPLACE_CACHE, id, name, new Double(latitude),
                new Double(longitude),
                mDbToGeocacheAdapter.sourceTypeToSourceName(sourceType, sourceName),
                cacheType.toInt(), difficulty, terrain, container, available, archived, visible);
    }

    public void startWriting() {
        sqliteProvider.get().beginTransaction();
    }

    public void stopWriting() {
        // TODO: abort if no writes--otherwise sqlite is unhappy.
        ISQLiteDatabase sqliteDatabase = sqliteProvider.get();
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }
}
