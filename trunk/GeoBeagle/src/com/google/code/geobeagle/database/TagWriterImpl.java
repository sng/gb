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

import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.util.Log;

public class TagWriterImpl implements TagWriter {

    private final Provider<ISQLiteDatabase> databaseProvider;
    private final SharedPreferences sharedPreferences;

    @Inject
    public TagWriterImpl(Provider<ISQLiteDatabase> databaseProvider,
            SharedPreferences sharedPreferences) {
        this.databaseProvider = databaseProvider;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void add(CharSequence geocacheId, Tag tag) {
        Log.d("GeoBeagle", "TagWriterImpl: " + geocacheId + ", " + tag);
        ISQLiteDatabase database = databaseProvider.get();
        database.delete("TAGS", "Cache", (String)geocacheId);
        database.insert("TAGS", new String[] {
                "Cache", "Id"
        }, new Object[] {
                geocacheId, tag.ordinal()
        });
        boolean showFoundCaches = sharedPreferences.getBoolean(EditPreferences.SHOW_FOUND_CACHES,
                false);
        boolean visible = showFoundCaches || !(tag == Tag.FOUND);
        if (!visible) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Visible", 0);
            database.update("CACHES", contentValues, "ID=?", new String[] {
                geocacheId.toString()
            });
        }
    }

    public boolean hasTag(CharSequence geocacheId, Tag tag) {
        ISQLiteDatabase mDatabase = null;
        try {
            mDatabase = databaseProvider.get();
        } catch (ProvisionException e) {
            Log.e("GeoBeagle", "Provision exception");
            return false;
        }
        boolean hasValue = mDatabase.hasValue("TAGS", new String[] {
                "Cache", "Id"
        }, new String[] {
                geocacheId.toString(), String.valueOf(tag.ordinal())
        });
        return hasValue;
    }

}
