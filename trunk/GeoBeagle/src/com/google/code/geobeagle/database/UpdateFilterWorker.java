package com.google.code.geobeagle.database;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.cachelist.presenter.UpdateFilterHandler;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.inject.Inject;

import roboguice.inject.ContextScoped;
import roboguice.util.RoboThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;

public class UpdateFilterWorker extends RoboThread {
    private final SharedPreferences sharedPreferences;
    private final DbFrontend dbFrontEnd;
    private final UpdateFlag updateFlag;
    private final UpdateFilterHandler updateFilterHandler;

    @ContextScoped
    public static class ClearFilterProgressDialog extends ProgressDialog {

        @Inject
        public ClearFilterProgressDialog(Context context) {
            super(context);
            // setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            setMessage("Clearing previous filter...");
            setIndeterminate(true);
            setTitle("Filtering caches");
            setCancelable(false);
        }

    }

    @ContextScoped
    public static class ApplyFilterProgressDialog extends ProgressDialog {

        @Inject
        public ApplyFilterProgressDialog(Context context) {
            super(context);
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            setMessage("Applying new filter...");
            setTitle("Filtering caches");
            setCancelable(false);
        }

    }
    @Inject
    public UpdateFilterWorker(SharedPreferences sharedPreferences,
            DbFrontend dbFrontEnd,
            UpdateFlag updateFlag,
            UpdateFilterHandler updateFilterHandler) {
        this.sharedPreferences = sharedPreferences;
        this.dbFrontEnd = dbFrontEnd;
        this.updateFlag = updateFlag;
        this.updateFilterHandler = updateFilterHandler;
    }

    @Override
    public void run() {
        boolean showFoundCaches = sharedPreferences.getBoolean(
                EditPreferences.SHOW_FOUND_CACHES, false);
        ISQLiteDatabase database = dbFrontEnd.getDatabase();
        database.execSQL("UPDATE CACHES SET Visible = 1");
        if (showFoundCaches) {
            updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                    UpdateFilterHandler.DISMISS_CLEAR_FILTER_PROGRESS, 0, 0));
            Editor editor = sharedPreferences.edit();
            editor.putBoolean("filter-dirty", false);
            editor.commit();
            return;
        }
        Cursor cursor = database
                .rawQuery("SELECT ROWID, Id FROM " + Database.TBL_CACHES, null);
        updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                UpdateFilterHandler.SHOW_APPLY_FILTER_PROGRESS, cursor.getCount(), 0));
        try {
            if (!cursor.moveToFirst())
                return;
            while (!cursor.isAfterLast()) {
                updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                        UpdateFilterHandler.INCREMENT_APPLY_FILTER_PROGRESS, 0, 0));
                int rowId = cursor.getInt(0);
                String cache = cursor.getString(1);
                boolean isFound = isFound(cache, database);
                if (isFound) {
                    database.execSQL("UPDATE CACHES SET Visible = 0 WHERE ROWID = ?", rowId);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("filter-dirty", false);
        editor.commit();
        updateFlag.setUpdatesEnabled(true);
        Log.d("GeoBeagle", "updating filter complete!");
        updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                UpdateFilterHandler.DISMISS_CLEAR_FILTER_PROGRESS, 0, 0));
    }

    private boolean isFound(String cache, ISQLiteDatabase database) {
        return database.hasValue("TAGS", new String[] {
                "Cache", "Id"
        }, new String[] {
                cache, String.valueOf(Tag.FOUND.ordinal())
        });
    }
}