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

package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.main.GeoBeagleDelegate.RadarViewRefresher;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteStringsFVsDnf;
import com.google.code.geobeagle.activity.main.view.FavoriteView;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.FavoriteView.FavoriteState;
import com.google.code.geobeagle.activity.main.view.FavoriteView.FavoriteViewDelegate;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.DbFrontend;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

@PrepareForTest( {
    KeyEvent.class, DateFormat.class, Intent.class, Bundle.class,
    FieldnoteStringsFVsDnf.class, FieldnoteLogger.class, GeoBeagleDelegate.class,
    Log.class, Uri.class, DatabaseDI.class, UrlQuerySanitizer.class,
    GeocacheFromIntentFactory.class, Util.class, Activity.class
})
@RunWith(PowerMockRunner.class)
public class GeoBeagleDelegateTest {

    @Test
    public void onResume() throws Exception {
        RadarView radarView = PowerMock.createMock(RadarView.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        GeoBeagle geobeagle = PowerMock.createMock(GeoBeagle.class);
        IncomingIntentHandler incomingIntentHandler = PowerMock
                .createMock(IncomingIntentHandler.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeocacheViewer geocacheViewer = PowerMock.createMock(GeocacheViewer.class);
        WebPageAndDetailsButtonEnabler webPageButtonEnabler = PowerMock
                .createMock(WebPageAndDetailsButtonEnabler.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        PowerMock.mockStatic(DatabaseDI.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);
        FavoriteViewDelegate favoriteViewDelegate = PowerMock
                .createMock(FavoriteViewDelegate.class);
        FavoriteState favoriteState = PowerMock.createMock(FavoriteState.class);

        radarView.handleUnknownLocation();
        geoFixProvider.onResume();
        EasyMock.expect(sharedPreferences.getBoolean("imperial", false)).andReturn(true);
        radarView.setUseImperial(true);
        EasyMock.expect(geobeagle.getIntent()).andReturn(intent);
        EasyMock.expect(
                incomingIntentHandler.maybeGetGeocacheFromIntent(intent, geocache, dbFrontend))
                .andReturn(geocache);

        geocacheViewer.set(geocache);
        webPageButtonEnabler.check();
        EasyMock.expect(geocache.getId()).andReturn("gc123");
        PowerMock.expectNew(FavoriteState.class, dbFrontend, "gc123")
                .andReturn(favoriteState);
        PowerMock.expectNew(FavoriteViewDelegate.class, favoriteView,
                favoriteState).andReturn(favoriteViewDelegate);
        favoriteView.setGeocache(favoriteViewDelegate);

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null,
                geobeagle, null, geocacheViewer, incomingIntentHandler, dbFrontend,
                radarView, sharedPreferences, webPageButtonEnabler,
                geoFixProvider, favoriteView);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onResume();
        PowerMock.verifyAll();
    }
    
    @Test
    public void onResumeNullGeocacheFromFactory() throws Exception {
        RadarView radarView = PowerMock.createMock(RadarView.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        GeoBeagle geobeagle = PowerMock.createMock(GeoBeagle.class);
        IncomingIntentHandler incomingIntentHandler = PowerMock
                .createMock(IncomingIntentHandler.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeocacheViewer geocacheViewer = PowerMock.createMock(GeocacheViewer.class);
        WebPageAndDetailsButtonEnabler webPageButtonEnabler = PowerMock
                .createMock(WebPageAndDetailsButtonEnabler.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        PowerMock.mockStatic(DatabaseDI.class);
        GeoFixProvider geoFixProvider = PowerMock
                .createMock(GeoFixProvider.class);
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        FavoriteState favoriteState = PowerMock.createMock(FavoriteState.class);
        FavoriteViewDelegate favoriteViewDelegate = PowerMock
                .createMock(FavoriteViewDelegate.class);

        radarView.handleUnknownLocation();
        geoFixProvider.onResume();
        EasyMock.expect(sharedPreferences.getBoolean("imperial", false))
                .andReturn(true);
        radarView.setUseImperial(true);
        EasyMock.expect(geobeagle.getIntent()).andReturn(intent);
        EasyMock.expect(
                incomingIntentHandler.maybeGetGeocacheFromIntent(intent,
                        geocache, dbFrontend)).andReturn(null);
        EasyMock.expect(
                geocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                        CacheType.NULL, 0, 0, 0)).andReturn(geocache);

        geocacheViewer.set(geocache);
        webPageButtonEnabler.check();
        EasyMock.expect(geocache.getId()).andReturn("gc123");
        PowerMock.expectNew(FavoriteState.class, dbFrontend, "gc123")
                .andReturn(favoriteState);
        PowerMock.expectNew(FavoriteViewDelegate.class, favoriteView,
                favoriteState).andReturn(favoriteViewDelegate);
        favoriteView.setGeocache(favoriteViewDelegate);
        
        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null,
                geobeagle, geocacheFactory, geocacheViewer, incomingIntentHandler,
                dbFrontend,
                radarView, sharedPreferences, webPageButtonEnabler,
                geoFixProvider, favoriteView);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onResume();
        PowerMock.verifyAll();
    }
    
    @Test
    public void testRadarViewRefresher() {
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        GeoFix geofix = PowerMock.createMock(GeoFix.class);
        RadarView radarView = 
            PowerMock.createMock(RadarView.class);
        EasyMock.expect(geoFixProvider.isProviderEnabled()).andReturn(true);
        EasyMock.expect(geoFixProvider.getLocation()).andReturn(geofix);
        EasyMock.expect(geoFixProvider.getAzimuth()).andReturn(18f);
        
        radarView.setLocation(geofix, 18);
        
        PowerMock.replayAll();
        RadarViewRefresher radarViewRefresher = new GeoBeagleDelegate.RadarViewRefresher(
                radarView, geoFixProvider);
        radarViewRefresher.forceRefresh();
        PowerMock.verifyAll();
    }
    

    @Test
    public void testRadarViewRefresherProviderDisabled() {
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        RadarView radarView = 
            PowerMock.createMock(RadarView.class);
        EasyMock.expect(geoFixProvider.isProviderEnabled()).andReturn(false);
        radarView.handleUnknownLocation();
        
        PowerMock.replayAll();
        RadarViewRefresher radarViewRefresher = new GeoBeagleDelegate.RadarViewRefresher(
                radarView, geoFixProvider);
        radarViewRefresher.forceRefresh();
        PowerMock.verifyAll();
    }
    
    @Test
    public void testLogFindClickListener() {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        View view = PowerMock.createMock(View.class);

        geoBeagle.showDialog(17);

        PowerMock.replayAll();
        new GeoBeagleDelegate.LogFindClickListener(geoBeagle, 17).onClick(view);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnKeyDown_Camera() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        KeyEvent keyEvent = PowerMock.createMock(KeyEvent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        File file = PowerMock.createMock(File.class);
        Uri uri = PowerMock.createMock(Uri.class);

        PowerMock.mockStatic(Uri.class);
        PowerMock.mockStatic(Log.class);
        PowerMock.mockStatic(DateFormat.class);
        PowerMock.mockStatic(System.class);

        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);
        EasyMock.expect(System.currentTimeMillis()).andReturn(1000L);
        EasyMock.expect(DateFormat.format(" yyyy-MM-dd kk.mm.ss.jpg", 1000L)).andReturn(
                " 2008-09-12 12.32.12.jpg");
        EasyMock.expect(geocache.getId()).andReturn("GCABC");
        PowerMock.expectNew(Intent.class, MediaStore.ACTION_IMAGE_CAPTURE).andReturn(intent);
        EasyMock.expect(intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)).andReturn(intent);
        EasyMock.expect(keyEvent.getRepeatCount()).andReturn(0);
        PowerMock.expectNew(File.class, "/sdcard/GeoBeagle/GCABC 2008-09-12 12.32.12.jpg")
                .andReturn(file);
        EasyMock.expect(Uri.fromFile(file)).andReturn(uri);
        geoBeagle.startActivityForResult(intent, GeoBeagleDelegate.ACTIVITY_REQUEST_TAKE_PICTURE);

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null,
                geoBeagle, null, null, null, null, null, null, null, null,
                null);
        geoBeagleDelegate.setGeocache(geocache);
        assertTrue(geoBeagleDelegate.onKeyDown(KeyEvent.KEYCODE_CAMERA, keyEvent));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnKeyDown_NotCamera() throws Exception {
        KeyEvent keyEvent = PowerMock.createMock(KeyEvent.class);

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null, null, null,
                null, null, null, null, null, null, null);
        assertFalse(geoBeagleDelegate.onKeyDown(KeyEvent.KEYCODE_A, keyEvent));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        Menu menu = PowerMock.createMock(Menu.class);

        EasyMock.expect(menuActions.onCreateOptionsMenu(menu)).andReturn(true);
        PowerMock.replayAll();

        assertTrue(new GeoBeagleDelegate.OptionsMenu(menuActions)
                .onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem item = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(item.getItemId()).andReturn(12);
        EasyMock.expect(menuActions.act(12)).andReturn(true);
        PowerMock.replayAll();

        assertTrue(new GeoBeagleDelegate.OptionsMenu(menuActions)
                .onOptionsItemSelected(item));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        ActivitySaver activitySaver = PowerMock.createMock(ActivitySaver.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        geoFixProvider.onPause();
        activitySaver.save(ActivityType.VIEW_CACHE, geocache);
        dbFrontend.closeDatabase();

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(
                activitySaver, null, null, null, null, dbFrontend, null,
                null, null, geoFixProvider, null);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnRestoreInstanceState() throws Exception {
        Bundle bundle = PowerMock.createMock(Bundle.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(bundle.getCharSequence(Geocache.ID)).andReturn("id1");
        EasyMock.expect(dbFrontend.loadCacheFromId("id1")).andReturn(geocache);

        PowerMock.replayAll();
        new GeoBeagleDelegate(null, null, null, null, null,
                dbFrontend, null, null, null, null, null)
                .onRestoreInstanceState(bundle);
        PowerMock.verifyAll();
    }

    @Test
    public void testSaveInstanceState() {
        Bundle bundle = PowerMock.createMock(Bundle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geocache.getId()).andReturn("id1");
        bundle.putCharSequence("id", "id1");
        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null, null, null,
                null, null, null, null, null, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onSaveInstanceState(bundle);
        PowerMock.verifyAll();

    }

    @Test
    public void testSetGet() {
        Geocache geocache = PowerMock.createMock(Geocache.class);

        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null, null, null,
                null, null, null, null, null, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        assertEquals(geocache, geoBeagleDelegate.getGeocache());
    }
}
