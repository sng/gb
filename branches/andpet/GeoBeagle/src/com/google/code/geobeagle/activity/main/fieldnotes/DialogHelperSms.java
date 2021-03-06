/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.main.fieldnotes;

import com.google.code.geobeagle.R;

import android.app.Dialog;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.widget.EditText;
import android.widget.TextView;

public class DialogHelperSms implements DialogHelper {
    private final EditText mEditText;
    private final boolean mFDnf;
    private final TextView mFieldnoteCaveat;
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;
    private final int mGeocacheIdLength;

    public DialogHelperSms(int geocacheIdLength, FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf,
            EditText editText, boolean dnf, TextView fieldnoteCaveat) {
        mGeocacheIdLength = geocacheIdLength;
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        mEditText = editText;
        mFDnf = dnf;
        mFieldnoteCaveat = fieldnoteCaveat;
    }

    @Override
    public void configureEditor() {
        final LengthFilter lengthFilter = new LengthFilter(
                160 - (mGeocacheIdLength + 1 + mFieldnoteStringsFVsDnf.getString(
                        R.array.fieldnote_code, mFDnf).length()));
        mEditText.setFilters(new InputFilter[] {
            lengthFilter
        });
    }

    @Override
    public void configureDialogText(Dialog dialog) {
        mFieldnoteCaveat.setText(R.string.sms_caveat);
        dialog.setTitle(R.string.log_cache_with_sms);
    }
}