package com.google.code.geobeagle;

import android.widget.EditText;

public class TooString {
	private final EditText editText;

	public TooString(EditText editText) {
		this.editText = editText;
	}

	public String tooString() {
		return editText.getText().toString();
	}
}
