
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.location.Location;
import android.location.LocationProvider;

import junit.framework.TestCase;

public class LocationViewerImplTest extends TestCase {

    public final void testCreate() {
        Location location = createMock(Location.class);
        expect(location.getLatitude()).andReturn(37.0);
        expect(location.getLongitude()).andReturn(122.0);
        expect(location.getTime()).andReturn(300L);
        MockableTextView lastUpdateTime = createMock(MockableTextView.class);
        lastUpdateTime.setText("16:00:00");
        MockableTextView coordinates = createMock(MockableTextView.class);
        coordinates.setText("37 00.000 122 00.000");
        MockableTextView status = createMock(MockableTextView.class);
        status.setText("OUT OF SERVICE");

        replay(location);
        replay(lastUpdateTime);
        replay(coordinates);
        new LocationViewerImpl(coordinates, lastUpdateTime, status, location);
        verify(location);
        verify(lastUpdateTime);
        verify(coordinates);
    }

    public final void testSetStatus() {
        Location location = createMock(Location.class);
        expect(location.getLatitude()).andReturn(37.0);
        expect(location.getLongitude()).andReturn(122.0);
        expect(location.getTime()).andReturn(300L);
        MockableTextView lastUpdateTime = createMock(MockableTextView.class);
        lastUpdateTime.setText("16:00:00");
        MockableTextView coordinates = createMock(MockableTextView.class);
        coordinates.setText("37 00.000 122 00.000");
        MockableTextView status = createMock(MockableTextView.class);
        status.setText("OUT OF SERVICE");
        status.setText("AVAILABLE");
        status.setText("TEMPORARILY UNAVAILABLE");

        replay(location);
        replay(lastUpdateTime);
        replay(coordinates);
        replay(status);
        LocationViewer lv = new LocationViewerImpl(coordinates, lastUpdateTime, status, location);
        lv.setStatus(LocationProvider.OUT_OF_SERVICE);
        lv.setStatus(LocationProvider.AVAILABLE);
        lv.setStatus(LocationProvider.TEMPORARILY_UNAVAILABLE);
        verify(location);
        verify(lastUpdateTime);
        verify(coordinates);
        verify(status);
    }
}
