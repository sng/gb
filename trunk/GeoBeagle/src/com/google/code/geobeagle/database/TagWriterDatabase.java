package com.google.code.geobeagle.database;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import android.content.ContentValues;
import android.util.Log;

class TagWriterDatabase {

    private final ContentValues hideColumn;
    private final Provider<ISQLiteDatabase> databaseProvider;

    @Inject
    public TagWriterDatabase(Provider<ISQLiteDatabase> databaseProvider) {
        this.databaseProvider = databaseProvider;
        hideColumn = new ContentValues();
        hideColumn.put("Visible", 0);
    }

    void addTag(CharSequence geocacheId, Tag tag) {
        ISQLiteDatabase database = databaseProvider.get();
        database.delete("TAGS", "Cache", (String)geocacheId);
        database.insert("TAGS", new String[] {
                "Cache", "Id"
        }, new Object[] {
                geocacheId, tag.ordinal()
        });
    }

    void hideCache(CharSequence geocacheId) {
        ISQLiteDatabase database = databaseProvider.get();
        database.update("CACHES", hideColumn, "ID=?", new String[] {
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

        boolean hasValue = database.hasValue("TAGS", new String[] {
                "Cache", "Id"
        }, new String[] {
                geocacheId.toString(), String.valueOf(tag.ordinal())
        });
        return hasValue;
    }
}
