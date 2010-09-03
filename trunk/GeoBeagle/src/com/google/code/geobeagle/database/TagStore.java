
package com.google.code.geobeagle.database;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import android.content.ContentValues;
import android.util.Log;

class TagStore {
    private static final String COLUMN_CACHE = "Cache";
    private static final String TBL_TAGS = "TAGS";
    private final ContentValues hideColumn;
    private final Provider<ISQLiteDatabase> databaseProvider;
    private final String[] columns;

    @Inject
    public TagStore(Provider<ISQLiteDatabase> databaseProvider) {
        this.databaseProvider = databaseProvider;
        hideColumn = new ContentValues();
        hideColumn.put("Visible", 0);
        columns = new String[] {
                COLUMN_CACHE, "Id"
        };
    }

    void addTag(CharSequence geocacheId, Tag tag) {
        ISQLiteDatabase database = databaseProvider.get();
        database.delete(TBL_TAGS, COLUMN_CACHE, (String)geocacheId);

        database.insert(TBL_TAGS, columns, new Object[] {
                geocacheId, tag.ordinal()
        });
    }

    void hideCache(CharSequence geocacheId) {
        ISQLiteDatabase database = databaseProvider.get();
        database.update(Database.TBL_CACHES, hideColumn, "ID=?", new String[] {
            geocacheId.toString()
        });
    }

    boolean hasTag(CharSequence geocacheId, Tag tag) {
        ISQLiteDatabase database = null;
        try {
            database = databaseProvider.get();
        } catch (ProvisionException e) {
            Log.e("GeoBeagle", "Provision exception");
            return false;
        }

        boolean hasValue = database.hasValue(TBL_TAGS, columns, new String[] {
                geocacheId.toString(), String.valueOf(tag.ordinal())
        });
        return hasValue;
    }
}
