
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.content.Intent;

import junit.framework.TestCase;

public class NearestCachesIntentStarterTest extends TestCase {

    public void testStartIntent() {
        GetCoordsToast getCoordsToast = createMock(GetCoordsToast.class);
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        Intent intent = createMock(Intent.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);

        expect(resourceProvider.getString(R.string.nearest_caches_url)).andReturn(
                "http://www.geocaching.com/nearest?slat=37.00000&lng=122.00000");
        getCoordsToast.show();
        expect(
                intentFactory
                        .createIntent(Intent.ACTION_VIEW,
                                "http://www.geocaching.com/nearest?slat=37.00000&lng=122.00000"))
                .andReturn(intent);
        activityStarter.startActivity(intent);
        replay(resourceProvider);
        replay(getCoordsToast);
        replay(intent);
        new NearestCachesIntentStarter(getCoordsToast, resourceProvider).startIntent(
                activityStarter, intentFactory, new Destination("37 00.0 122 00.0"));
        verify(resourceProvider);
        verify(getCoordsToast);
        verify(intent);
    }

}
