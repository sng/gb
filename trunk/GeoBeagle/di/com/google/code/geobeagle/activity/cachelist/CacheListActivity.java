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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.GeoBeagleApplication;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.CacheList;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import roboguice.activity.GuiceListActivity;
import roboguice.application.GuiceApplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class CacheListActivity extends GuiceListActivity {
    static class OnClickCancel implements OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    }

    private CacheListDelegate mCacheListDelegate;
    private Injector injector;
    
    // This is the ctor that Android will use.
    public CacheListActivity() {
    }

    // This is the ctor for testing.
    public CacheListActivity(CacheListDelegate cacheListDelegate) {
        mCacheListDelegate = cacheListDelegate;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mCacheListDelegate.onContextItemSelected(item) || super.onContextItemSelected(item);
    }

    @Override
    public Injector getInjector() {
        if (injector == null) {
            ((GeoBeagleApplication)getApplication()).initInstanceMembers();
            ArrayList<Module> modules = new ArrayList<Module>();
            modules.add(new CacheListModule());
            injector = createInjector(modules);
        }
        return injector;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GeoBeagle", "CacheListActivity onCreate");
        final Injector injector = this.getInjector();
        final InflatedGpsStatusWidget inflatedGpsStatusWidget = injector.getInstance(Key.get(
                InflatedGpsStatusWidget.class, CacheList.class));
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = injector.getInstance(Key.get(
                GpsStatusWidgetDelegate.class, CacheList.class));
        
        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        mCacheListDelegate = injector.getInstance(CacheListDelegate.class);

        mCacheListDelegate.onCreate();
    }

    @Override
    public Dialog onCreateDialog(int idDialog) {
        // idDialog must be CACHE_LIST_DIALOG_CONFIRM_DELETE.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View confirmDeleteCacheView = LayoutInflater.from(this).inflate(
                R.layout.confirm_delete_cache, null);

        builder.setNegativeButton(R.string.confirm_delete_negative, new OnClickCancel());
        builder.setView(confirmDeleteCacheView);

        return mCacheListDelegate.onCreateDialog(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mCacheListDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mCacheListDelegate.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.d("GeoBeagle", "CacheListActivity onPause");
        /*
         * cacheListDelegate closes the database, it must be called before
         * super.onPause because the guice activity onPause nukes the database
         * object from the guice map.
         */
        mCacheListDelegate.onPause();
        super.onPause();
        Log.d("GeoBeagle", "CacheListActivity onPauseComplete");
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        mCacheListDelegate.onPrepareDialog(dialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "CacheListActivity onResume");
        mCacheListDelegate.onResume();
    }

}
