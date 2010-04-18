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

package com.google.code.geobeagle.activity.map;

import static org.junit.Assert.assertEquals;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator.IconFactory;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        OverlayItem.class, CacheItem.class
})
public class CacheItemTest {
    @Test
    public void testCreateCacheItem() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeoPoint geoPoint = PowerMock.createMock(GeoPoint.class);
        IconFactory iconFactory = PowerMock.createMock(IconFactory.class);
        MapViewBitmapCopier mapViewBitmapCopier = PowerMock
                .createMock(MapViewBitmapCopier.class);
        Drawable icon = PowerMock.createMock(Drawable.class);

        EasyMock.expect(geocache.getGeoPoint()).andReturn(geoPoint);
        EasyMock.expect(geocache.getId()).andReturn("GC123");
        EasyMock.expect(iconFactory.createMapViewIcon(geocache, mapViewBitmapCopier)).andReturn(icon);
       
        PowerMock.suppressConstructor(CacheItem.class);
        PowerMock.suppressMethod(CacheItem.class, "setMarker");

        PowerMock.replayAll();
        new CacheItemFactory(iconFactory, mapViewBitmapCopier)
                .createCacheItem(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheDrawables() {
        Resources resources = PowerMock.createMock(Resources.class);
        Drawable drawable = PowerMock.createMock(Drawable.class);

        EasyMock.expect(resources.getDrawable(EasyMock.anyInt())).andReturn(drawable).anyTimes();
        EasyMock.expect(drawable.getIntrinsicWidth()).andReturn(18).anyTimes();
        EasyMock.expect(drawable.getIntrinsicHeight()).andReturn(22).anyTimes();
        drawable.setBounds(-9, -22, 9, 0);
        EasyMock.expectLastCall().anyTimes();
        
        PowerMock.replayAll();
        assertEquals(drawable, new CacheDrawables(resources).get(CacheType.MULTI));
        PowerMock.verifyAll();
    }
}
