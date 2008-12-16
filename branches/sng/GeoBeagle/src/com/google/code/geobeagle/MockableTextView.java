
package com.google.code.geobeagle;

import android.widget.TextView;

public class MockableTextView {

    private TextView mTextView;

    public MockableTextView(TextView textView) {
        this.mTextView = textView;
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

    public void setEnabled(boolean enabled) {
        mTextView.setEnabled(enabled);
    }

}
