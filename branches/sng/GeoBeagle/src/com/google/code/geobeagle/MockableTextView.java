package com.google.code.geobeagle;

import android.widget.TextView;

public class MockableTextView {

	private TextView textView;

	public MockableTextView(TextView textView) {
		this.textView = textView;
	}

	public void setText(CharSequence text) {
		textView.setText(text);
	}

	public CharSequence getText() {
		return textView.getText();
	}

	public void setEnabled(boolean enabled) {
		textView.setEnabled(enabled);
	}

}
