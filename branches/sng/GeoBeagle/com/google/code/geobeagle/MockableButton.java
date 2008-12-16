package com.google.code.geobeagle;

import android.view.View.OnClickListener;
import android.widget.Button;

public class MockableButton extends MockableTextView {

	private Button button;

	public MockableButton(Button button) {
		super(button);
		this.button = button;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		button.setOnClickListener(onClickListener);
	}

	public void setTextColor(int red) {
		button.setTextColor(red);
	}

}
