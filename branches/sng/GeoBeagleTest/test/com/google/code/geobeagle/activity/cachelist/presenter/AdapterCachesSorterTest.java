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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@PrepareForTest( {})
@RunWith(PowerMockRunner.class)
public class AdapterCachesSorterTest {

    @Test
    public void testAdapterCachesSorter() {
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        SortStrategy sortStrategy = PowerMock.createMock(SortStrategy.class);
        ArrayList<GeocacheVector> arrayList = new ArrayList<GeocacheVector>();

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(cacheListData.get()).andReturn(arrayList);
        EasyMock.expect(locationControlBuffered.getSortStrategy()).andReturn(sortStrategy);
        sortStrategy.sort(arrayList);

        PowerMock.replayAll();
        new AdapterCachesSorter(cacheListData, timing, locationControlBuffered).refresh();
        PowerMock.verifyAll();
    }
}
