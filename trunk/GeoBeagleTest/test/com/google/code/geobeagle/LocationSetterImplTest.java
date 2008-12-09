package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.notNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.GregorianCalendar;

import junit.framework.TestCase;
import android.location.Location;
import android.view.View.OnFocusChangeListener;

import com.google.code.geobeagle.GpsControl;
import com.google.code.geobeagle.LocationSetter;
import com.google.code.geobeagle.LocationSetterImpl;
import com.google.code.geobeagle.MockableEditText;

public class LocationSetterImplTest extends TestCase {
	private static final String LOCATION1 = "37 11.1 122 22.2 #foobar";
	private static final String LOCATION2 = "38 33.3 122 44.4 #baz";

	public void testSaveLocation() {
		MockableEditText editText = createMock(MockableEditText.class);
		editText.setOnFocusChangeListener((OnFocusChangeListener) notNull());
		editText.setText(LOCATION1);
		editText.setText(LOCATION2);

		replay(editText);
		LocationSetter lsi = new LocationSetterImpl(null, editText, new GpsControl() {
			public Location getLocation() {
				assertTrue(false);
				return null;
			}
		});
		lsi.setLocation(LOCATION1);
		lsi.setLocation(LOCATION2);
		assertEquals(LOCATION1, lsi.getPreviousLocations().get(0));
		assertEquals(LOCATION2, lsi.getPreviousLocations().get(1));
		verify(editText);
	}

	public void testMyLocation() {
		final Location location = createMock(Location.class);
		MockableEditText editText = createMock(MockableEditText.class);
		editText.setOnFocusChangeListener((OnFocusChangeListener) notNull());
		editText.setText("37 07.380  122 20.700 # GPS at 4:25 AM");
		expect(location.getLatitude()).andReturn(37.123);
		expect(location.getLongitude()).andReturn(122.345);
		expect(location.getTime()).andReturn(
				new GregorianCalendar(2008, 12, 5, 4, 25, 10).getTime().getTime());
		replay(location);
		replay(editText);
		LocationSetter lsi = new LocationSetterImpl(null, editText, new GpsControl() {
			public Location getLocation() {
				return location;
			}
		});
		lsi.setLocation(null);
		verify(location);
		verify(editText);
	}
}
