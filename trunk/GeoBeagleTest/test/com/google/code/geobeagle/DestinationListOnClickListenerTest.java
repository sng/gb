package com.google.code.geobeagle;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;

import com.google.code.geobeagle.DestinationListOnClickListener.DestinationListDialogOnClickListener;

public class DestinationListOnClickListenerTest extends TestCase {
	OnClickListener mockListener = createMock(OnClickListener.class);

	public void testOnClick() {
		LocationSetter locationSetter = createMock(LocationSetter.class);
		AlertDialog.Builder dialogBuilder = createMock(AlertDialog.Builder.class);
		AlertDialog alertDialog = createMock(AlertDialog.class);
		CharSequence[] descriptions = new CharSequence[]{"SFO", "OAK"};
		final CharSequence[] locations = new CharSequence[]{"37 122 etc", "37 122 foo"};

		expect(dialogBuilder.setTitle(R.string.select_destination)).andReturn(dialogBuilder);
		expect(
				dialogBuilder.setItems(aryEq(new CharSequence[]{
						DestinationListOnClickListener.MY_LOCATION, "OAK", "SFO"}),
						(OnClickListener) eq(mockListener))).andReturn(dialogBuilder);

		expect(dialogBuilder.create()).andReturn(alertDialog);
		alertDialog.show();

		replay(dialogBuilder);
		replay(alertDialog);
		DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
		for (int ix = 0; ix < descriptions.length; ix++) {
			descriptionsAndLocations.add(descriptions[ix], locations[ix]);
		}

		DestinationListOnClickListener destinationListOnClickListener = new DestinationListOnClickListener(
				descriptionsAndLocations, locationSetter, dialogBuilder) {
			protected OnClickListener createDestinationListDialogOnClickListener(
					List<CharSequence> previousLocations) {
				assertEquals(Arrays.asList(null, locations[1], locations[0]), previousLocations);
				return mockListener;
			}
		};
		destinationListOnClickListener.onClick(null);
		verify(dialogBuilder);
		verify(alertDialog);
	}

	public void testDestinationListDialogOnClickListener() {
		LocationSetter locationSetter = createMock(LocationSetter.class);
		CharSequence[] locations = new CharSequence[]{null, "37 122 foo", "37 122 etc",};
		locationSetter.setLocation("37 122 foo");

		replay(locationSetter);
		DestinationListDialogOnClickListener dldocl = new DestinationListDialogOnClickListener(
				Arrays.asList(locations), locationSetter);
		dldocl.onClick(null, 1);
		verify(locationSetter);
	}
}
