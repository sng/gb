
package com.google.code.geobeagle.ui;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.Geocache.Source;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate.CancelButtonOnClickListener;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate.EditCache;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate.SetButtonOnClickListener;

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
    public void CancelButtonOnClickListenerTest() {
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
    public void EditCacheActivityDelegateOnCreateTest() {
        Activity activity = PowerMock.createMock(Activity.class);

        activity.setContentView(R.layout.cache_edit);

        PowerMock.replayAll();
        new EditCacheActivityDelegate(activity, null, null).onCreate(null);
        PowerMock.verifyAll();
    }

    @Test
    public void EditCacheActivityOnResumeTest() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);
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

        PowerMock.expectNew(EditCache.class, (EditText)id, (EditText)name, (EditText)latitude,
                (EditText)longitude).andReturn(editCache);
        editCache.set(geocache);
        PowerMock.expectNew(SetButtonOnClickListener.class, activity, editCache, locationSaver)
                .andReturn(setButtonOnClickListener);
        EasyMock.expect(activity.findViewById(R.id.edit_set)).andReturn(set);
        set.setOnClickListener(setButtonOnClickListener);

        EasyMock.expect(activity.findViewById(R.id.edit_cancel)).andReturn(cancel);
        cancel.setOnClickListener(cancelButtonOnClickListener);

        PowerMock.replayAll();
        EditCacheActivityDelegate editCacheActivityDelegate = new EditCacheActivityDelegate(
                activity, cancelButtonOnClickListener, locationSaver);
        editCacheActivityDelegate.onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void GeocacheViewGetAndSetTest() {
        EditText id = PowerMock.createMock(EditText.class);
        EditText name = PowerMock.createMock(EditText.class);
        EditText latitude = PowerMock.createMock(EditText.class);
        EditText longitude = PowerMock.createMock(EditText.class);

        id.setText("GC123");
        name.setText("a cache");
        latitude.setText("37 30.000");
        longitude.setText("-122 24.000");
        EasyMock.expect(latitude.requestFocus()).andReturn(true);

        EasyMock.expect(id.getText()).andReturn(new StubEditable("GC123"));
        EasyMock.expect(name.getText()).andReturn(new StubEditable("a cache"));
        EasyMock.expect(latitude.getText()).andReturn(new StubEditable("37 15.50"));
        EasyMock.expect(longitude.getText()).andReturn(new StubEditable("-122 30.000"));

        PowerMock.replayAll();
        EditCache editCache = new EditCache(id, name, latitude, longitude);
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.4, Source.GPX, null);
        editCache.set(geocache);
        geocache = editCache.get();
        assertEquals("GC123", geocache.getId().toString());
        assertEquals("a cache", geocache.getName().toString());
        assertEquals(37.25833, geocache.getLatitude(), 0.00001);
        assertEquals(-122.5, geocache.getLongitude(), 0.0001);
        PowerMock.verifyAll();
    }

    @Test
    public void SetButtonOnClickListenerTest() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        EditCache editCache = PowerMock.createMock(EditCache.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);

        locationSaver.saveLocation(geocache);
        EasyMock.expect(intent.setAction(CacheListDelegate.SELECT_CACHE)).andReturn(intent);
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
