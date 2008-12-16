package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;
import android.location.Location;

public class LocationViewerImplTest extends TestCase {

	public final void testSetLocationLocation() {
		Location location = createMock(Location.class);
		expect(location.getLatitude()).andReturn(37.0);
		expect(location.getLongitude()).andReturn(122.0);
		expect(location.getTime()).andReturn(300L);
		MockableButton button = createMock(MockableButton.class);
		button.setEnabled(false);
		button.setEnabled(true);
		button.setText("GPS@16:00:00");
		MockableTextView coordinates = createMock(MockableTextView.class);
		coordinates.setText("37 00.000 122 00.000");
		replay(location);
		replay(button);
		replay(coordinates);
		new LocationViewerImpl(button, coordinates, location);
		verify(location);
		verify(button);
		verify(coordinates);
	}

}
