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

package com.google.code.geobeagle.gpsstatuswidget;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.Lag;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LagImpl;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LagNull;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastKnownLocation;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastKnownLocationUnavailable;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastLocationUnknown;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        TextView.class, TextLagUpdater.class
})
public class TextLagUpdaterTest {
    @Test
    public void testLagImpl() {
        assertEquals("1s", new LagImpl(5000).getFormatted(6000));
    }

    @Test
    public void testLagNull() {
        assertEquals("", new LagNull().getFormatted(123));
    }

    @Test
    public void testLastKnownLocation() throws Exception {
        LagImpl lagImpl = PowerMock.createMock(LagImpl.class);

        PowerMock.expectNew(LagImpl.class, 1000L).andReturn(lagImpl);

        PowerMock.replayAll();
        assertEquals(lagImpl, new LastKnownLocation(1000L).getLag());
        PowerMock.verifyAll();
    }

    @Test
    public void testLastKnownLocationUnavailable() {
        LagNull lagNull = PowerMock.createMock(LagNull.class);

        PowerMock.replayAll();
        assertEquals(lagNull, new LastKnownLocationUnavailable(lagNull).getLag());
        PowerMock.verifyAll();
    }

    @Test
    public void testLastLocationUnknown() throws Exception {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        Lag lag = PowerMock.createMock(Lag.class);
        LastKnownLocation lastKnownLocation = PowerMock.createMock(LastKnownLocation.class);
        Location location = PowerMock.createMock(Location.class);

        EasyMock.expect(combinedLocationManager.getLastKnownLocation()).andReturn(location);
        EasyMock.expect(location.getTime()).andReturn(1000L);
        PowerMock.expectNew(LastKnownLocation.class, 1000L).andReturn(lastKnownLocation);
        EasyMock.expect(lastKnownLocation.getLag()).andReturn(lag);

        PowerMock.replayAll();
        assertEquals(lag, new LastLocationUnknown(combinedLocationManager, null).getLag());
        PowerMock.verifyAll();
    }

    @Test
    public void testLastLocationUnknown_Null() throws Exception {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        LagNull lagNull = PowerMock.createMock(LagNull.class);
        LastKnownLocationUnavailable lastKnownLocationUnavailable = PowerMock
                .createMock(LastKnownLocationUnavailable.class);

        EasyMock.expect(combinedLocationManager.getLastKnownLocation()).andReturn(null);
        EasyMock.expect(lastKnownLocationUnavailable.getLag()).andReturn(lagNull);

        PowerMock.replayAll();
        assertEquals(lagNull, new LastLocationUnknown(combinedLocationManager,
                lastKnownLocationUnavailable).getLag());
        PowerMock.verifyAll();
    }

    @Test
    public void testReset() throws Exception {
        Lag lag = PowerMock.createMock(Lag.class);
        LastKnownLocation lastLocation = PowerMock.createMock(LastKnownLocation.class);
        TextView textLag = PowerMock.createMock(TextView.class);
        Time time = PowerMock.createMock(Time.class);

        PowerMock.expectNew(LastKnownLocation.class, 2000L).andReturn(lastLocation);
        EasyMock.expect(lastLocation.getLag()).andReturn(lag);
        EasyMock.expect(time.getCurrentTime()).andReturn(5000L);
        EasyMock.expect(lag.getFormatted(5000L)).andReturn("4s");
        textLag.setText("4s");

        PowerMock.replayAll();
        final TextLagUpdater textLagUpdater = new TextLagUpdater(null, textLag, time);
        textLagUpdater.reset(2000);
        textLagUpdater.updateTextLag();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetDisabled() throws Exception {
        TextView textLag = PowerMock.createMock(TextView.class);

        textLag.setText("");

        PowerMock.replayAll();
        new TextLagUpdater(null, textLag, null).setDisabled();
        PowerMock.verifyAll();
    }

    @Test
    public void testTimeFormatter() {
        assertEquals("10s", TextLagUpdater.formatTime(10));
        assertEquals("8m 0s", TextLagUpdater.formatTime(480));
        assertEquals("10m 5s", TextLagUpdater.formatTime(605));
        assertEquals("1h 1m", TextLagUpdater.formatTime(3660));
    }

    @Test
    public void testUpdateTextLag() {
        Lag lag = PowerMock.createMock(Lag.class);
        LastLocationUnknown lastLocationUnknown = PowerMock.createMock(LastLocationUnknown.class);
        TextView textLag = PowerMock.createMock(TextView.class);
        Time time = PowerMock.createMock(Time.class);

        EasyMock.expect(lastLocationUnknown.getLag()).andReturn(lag);
        EasyMock.expect(time.getCurrentTime()).andReturn(5000L);
        EasyMock.expect(lag.getFormatted(5000L)).andReturn("5s");
        textLag.setText("5s");

        PowerMock.replayAll();
        final TextLagUpdater textLagUpdater = new TextLagUpdater(lastLocationUnknown, textLag, time);
        textLagUpdater.updateTextLag();
        PowerMock.verifyAll();
    }
}
