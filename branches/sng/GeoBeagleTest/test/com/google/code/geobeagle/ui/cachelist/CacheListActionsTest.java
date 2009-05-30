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

package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.Geocache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
public class CacheListActionsTest {

    @Test
    public void testActionView() {
        Intent intent = PowerMock.createMock(Intent.class);
        Context context = PowerMock.createMock(Context.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(geocacheVectors.get(34)).andReturn(geocacheVector);
        expect(geocacheVector.getGeocache()).andReturn(geocache);
        expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        context.startActivity(intent);

        PowerMock.replayAll();
        new ContextActionView(geocacheVectors, context, intent).act(34);
        PowerMock.verifyAll();
    }
}
