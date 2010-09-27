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

package com.google.code.geobeagle.cachelist;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GeocacheVectors.class
})
public class GeocacheVectorsTest {

    @Test
    public void testAddLocations() throws Exception {
        LocationControlBuffered here = createMock(LocationControlBuffered.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>(0);

        PowerMock.expectNew(GeocacheVector.class, geocache, here).andReturn(geocacheVector);
        locations.add(geocache);

        PowerMock.replayAll();
        GeocacheVectors geocacheVectors = new GeocacheVectors();
        geocacheVectors.addLocations(locations, here);
        assertEquals(geocacheVector, geocacheVectors.get(0));
        PowerMock.verifyAll();
    }

    @Test
    public void testDelete() {
        GeocacheVector destinationVector1 = createMock(GeocacheVector.class);
        GeocacheVector destinationVector2 = createMock(GeocacheVector.class);

        ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>(0);

        GeocacheVectors geocacheVectors = new GeocacheVectors();
        geocacheVectors.add(destinationVector1);
        geocacheVectors.add(destinationVector2);
        geocacheVectors.remove(0);
        assertEquals(destinationVector1, geocacheVectors.get(0));
    }

    @Test
    public void testGetGeocacheVectorsList() {
        ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>(0);

        GeocacheVectors geocacheVectors = new GeocacheVectors();
        assertEquals(geocacheVectorsList, geocacheVectors.getGeocacheVectorsList());
    }

    @Test
    public void testReset() {
        GeocacheVector destinationVector = createMock(GeocacheVector.class);

        ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>(0);

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors();
        geocacheVectors.add(destinationVector);
        geocacheVectors.reset(12); // can't test ensureCapacity.
        assertEquals(0, geocacheVectors.size());
        verify(destinationVector);
    }
}
