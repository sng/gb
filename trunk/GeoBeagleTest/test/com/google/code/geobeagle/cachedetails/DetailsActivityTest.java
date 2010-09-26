
package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.cachedetails.DetailsActivityTest.DetailsActivityTester;
import com.google.code.geobeagle.shakewaker.ShakeWaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import roboguice.activity.GuiceActivity;

import android.app.ListActivity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

import junit.framework.TestCase;

@PrepareForTest({
        Handler.class, ListActivity.class, Log.class, CacheListRefresh.class, TextView.class,
        Timing.class, Calendar.class, DetailsActivity.class, GuiceActivity.class,
        DetailsActivityTester.class
})
@RunWith(PowerMockRunner.class)
public class DetailsActivityTest extends TestCase {

    private ShakeWaker shakeWaker;

    static class DetailsActivityTester extends DetailsActivity {
        DetailsActivityTester(ShakeWaker shakeWaker) {
            super(shakeWaker);
        }

        public void testPause() {
            onPause();
        }

        public void testResume() {
            onResume();
        }
    }

    @Override
    @Before
    public void setUp() {
        shakeWaker = PowerMock.createMock(ShakeWaker.class);
        PowerMock.suppressConstructor(GuiceActivity.class);
        PowerMock.suppressMethod(GuiceActivity.class, "onPause");
    }

    @Test
    public void testOnPause() {
        shakeWaker.unregister();

        PowerMock.replayAll();
        DetailsActivityTester detailsActivity = new DetailsActivityTester(shakeWaker);
        detailsActivity.testPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        shakeWaker.register();

        PowerMock.replayAll();
        DetailsActivityTester detailsActivity = new DetailsActivityTester(shakeWaker);
        detailsActivity.testResume();
        PowerMock.verifyAll();
    }
}
