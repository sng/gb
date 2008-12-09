package com.google.code.geobeagle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;

public class LocationOnKeyListener implements OnKeyListener {

	private final Button cachePage;
	private final TooString tooString;

	public LocationOnKeyListener(Button cachePage, TooString editText) {
		this.cachePage = cachePage;
		this.tooString = editText;
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		boolean isCache = false;
		try {
			isCache = (new Destination(tooString.tooString())).getDescription()
					.startsWith("GC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		cachePage.setEnabled(isCache);
		return false;
	}

}