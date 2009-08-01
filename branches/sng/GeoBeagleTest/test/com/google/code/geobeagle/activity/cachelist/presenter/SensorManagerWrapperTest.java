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

import com.google.code.geobeagle.CompassListener;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.hardware.SensorManager;

@RunWith(PowerMockRunner.class)
public class SensorManagerWrapperTest {
    @Test
    public void SensorManagerTest() {
        SensorManager sensorManager = PowerMock.createMock(SensorManager.class);
        CompassListener compassListener = PowerMock.createMock(CompassListener.class);

        EasyMock.expect(sensorManager.registerListener(compassListener, 0, 1)).andReturn(true);
        sensorManager.unregisterListener(compassListener);

        PowerMock.replayAll();
        sensorManager.registerListener(compassListener, 0, 1);
        sensorManager.unregisterListener(compassListener);
        PowerMock.verifyAll();
    }
}
