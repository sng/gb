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

package com.google.code.geobeagle.activity.main.view;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.view.EditCacheActivityDelegate.CancelButtonOnClickListener;
import com.google.code.geobeagle.activity.main.view.EditCacheActivityDelegate.EditCache;
import com.google.code.geobeagle.activity.main.view.EditCacheActivityDelegate.SetButtonOnClickListener;
import com.google.code.geobeagle.database.LocationSaver;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Activity.class, EditText.class, Intent.class, SetButtonOnClickListener.class,
        EditCacheActivityDelegate.class
})
public class EditCacheActivityDelegateTest {

    @Test
    public void testCancelButtonOnClickListener() {
        Activity activity = PowerMock.createMock(Activity.class);

        activity.setResult(-1, null);
        activity.finish();

        PowerMock.replay(activity);
        CancelButtonOnClickListener cancelButtonOnClickListener = new CancelButtonOnClickListener(
                activity);
        cancelButtonOnClickListener.onClick(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testEditCacheActivityDelegateOnCreate() {
        Activity activity = PowerMock.createMock(Activity.class);

        activity.setContentView(R.layout.cache_edit);

        PowerMock.replayAll();
        new EditCacheActivityDelegate(activity, null, null, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testEditCacheActivityOnResume() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        EditText id = PowerMock.createMock(EditText.class);
        EditText name = PowerMock.createMock(EditText.class);
        EditText latitude = PowerMock.createMock(EditText.class);
        EditText longitude = PowerMock.createMock(EditText.class);
        Button set = PowerMock.createMock(Button.class);
        SetButtonOnClickListener setButtonOnClickListener = PowerMock
                .createMock(SetButtonOnClickListener.class);
        EditCache editCache = PowerMock.createMock(EditCache.class);
        CancelButtonOnClickListener cancelButtonOnClickListener = PowerMock
                .createMock(CancelButtonOnClickListener.class);
        Button cancel = PowerMock.createMock(Button.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);

        EasyMock.expect(activity.getIntent()).andReturn(intent);
        EasyMock.expect(intent.<Geocache> getParcelableExtra("geocache")).andReturn(geocache);
        EasyMock.expect(activity.findViewById(R.id.edit_id)).andReturn(id);
        EasyMock.expect(activity.findViewById(R.id.edit_name)).andReturn(name);
        EasyMock.expect(activity.findViewById(R.id.edit_latitude)).andReturn(latitude);
        EasyMock.expect(activity.findViewById(R.id.edit_longitude)).andReturn(longitude);
        PowerMock.expectNew(EditCache.class, geocacheFactory, id, name, latitude, longitude)
                .andReturn(editCache);
        editCache.set(geocache);

        PowerMock.expectNew(SetButtonOnClickListener.class, activity, editCache, locationSaver)
                .andReturn(setButtonOnClickListener);
        EasyMock.expect(activity.findViewById(R.id.edit_set)).andReturn(set);
        set.setOnClickListener(setButtonOnClickListener);

        EasyMock.expect(activity.findViewById(R.id.edit_cancel)).andReturn(cancel);
        cancel.setOnClickListener(cancelButtonOnClickListener);

        PowerMock.replayAll();
        new EditCacheActivityDelegate(activity, cancelButtonOnClickListener, geocacheFactory,
                locationSaver).onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testGeocacheViewGetAndSet() {
        EditText id = PowerMock.createMock(EditText.class);
        EditText name = PowerMock.createMock(EditText.class);
        EditText latitude = PowerMock.createMock(EditText.class);
        EditText longitude = PowerMock.createMock(EditText.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geocache.getId()).andReturn("GC123");
        id.setText("GC123");
        EasyMock.expect(geocache.getName()).andReturn("a cache");
        name.setText("a cache");
        EasyMock.expect(geocache.getLatitude()).andReturn(37.5);
        latitude.setText("37 30.000");
        EasyMock.expect(geocache.getLongitude()).andReturn(-122.4);
        longitude.setText("-122 24.000");

        EasyMock.expect(latitude.requestFocus()).andReturn(true);
        StubEditable editableId = new StubEditable("GC123");
        EasyMock.expect(id.getText()).andReturn(editableId);
        StubEditable editableName = new StubEditable("a cache");
        EasyMock.expect(name.getText()).andReturn(editableName);
        StubEditable editableLatitude = new StubEditable("37 0.00");
        EasyMock.expect(latitude.getText()).andReturn(editableLatitude);
        StubEditable editableLongitude = new StubEditable("-122 30.000");
        EasyMock.expect(longitude.getText()).andReturn(editableLongitude);
        EasyMock.expect(geocache.getSourceType()).andReturn(Source.GPX);
        EasyMock.expect(geocache.getSourceName()).andReturn("source");
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.TRADITIONAL);
        EasyMock.expect(geocache.getDifficulty()).andReturn(3);
        EasyMock.expect(geocache.getTerrain()).andReturn(2);
        EasyMock.expect(geocache.getContainer()).andReturn(4);

        EasyMock.expect(
                geocacheFactory.create(editableId, editableName, 37, -122.5, Source.GPX, "source",
                        CacheType.TRADITIONAL, 3, 2, 4)).andReturn(geocache);

        PowerMock.replayAll();
        EditCache editCache = new EditCache(geocacheFactory, id, name, latitude, longitude);
        editCache.set(geocache);
        assertEquals(geocache, editCache.get());
        PowerMock.verifyAll();
    }

    @Test
    public void testSetButtonOnClickListener() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        EditCache editCache = PowerMock.createMock(EditCache.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);

        locationSaver.saveLocation(geocache);
        EasyMock.expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        PowerMock.expectNew(Intent.class).andReturn(intent);
        EasyMock.expect(editCache.get()).andReturn(geocache);
        EasyMock.expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        activity.setResult(0, intent);
        activity.finish();

        PowerMock.replayAll();
        SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(activity,
                editCache, locationSaver);
        setButtonOnClickListener.onClick(null);
        PowerMock.verifyAll();
    }
}
