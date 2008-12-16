package com.google.code.geobeagle;

import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class MockableEditText {
	private final EditText editText;

	public MockableEditText(EditText editText) {
		this.editText = editText;
	}

	public void setText(CharSequence s) {
		editText.setText(s);
	}

	public CharSequence getText() {
		return editText.getText();
	}

	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		editText.setOnFocusChangeListener(l);
	}
}
