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
import com.google.code.geobeagle.GeoBeaglePackageModule.DefaultSharedPreferences;
import com.google.code.geobeagle.Geocache.GeocacheId;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class FieldnoteLogger {
    // TODO: share one onClickCancel across app.
    public static class OnClickCancel implements OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    }

    public interface OnClickOkFactory {
        public OnClickOk create(EditText editText, boolean dnf);
    }

    public static class OnClickOk implements OnClickListener {
        private final CacheLogger mCacheLogger;
        private final boolean mDnf;
        private final EditText mEditText;
        private final CharSequence mGeocacheId;

        @Inject
        public OnClickOk(@GeocacheId CharSequence geocacheId, @Assisted EditText editText,
                CacheLogger cacheLogger, @Assisted boolean dnf) {
            mGeocacheId = geocacheId;
            mEditText = editText;
            mCacheLogger = cacheLogger;
            mDnf = dnf;
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            mCacheLogger.log(mGeocacheId, mEditText.getText(), mDnf);
        }
    }

    public static interface FieldnoteLoggerFactory {
        public FieldnoteLogger create(DialogHelperSms dialogHelperSms);
    }

    private final DialogHelperCommon mDialogHelperCommon;
    private final DialogHelperFile mDialogHelperFile;
    private final DialogHelperSms mDialogHelperSms;
    private final SharedPreferences mSharedPreferences;

    @Inject
    public FieldnoteLogger(DialogHelperCommon dialogHelperCommon,
            DialogHelperFile dialogHelperFile, @Assisted DialogHelperSms dialogHelperSms,
            @DefaultSharedPreferences SharedPreferences sharedPreferences) {
        mDialogHelperSms = dialogHelperSms;
        mDialogHelperFile = dialogHelperFile;
        mDialogHelperCommon = dialogHelperCommon;
        mSharedPreferences = sharedPreferences;
    }

    public void onPrepareDialog(Dialog dialog, String localDate, boolean dnf) {
        final boolean fieldNoteTextFile = mSharedPreferences.getBoolean("field-note-text-file",
                false);
        DialogHelper dialogHelper = fieldNoteTextFile ? mDialogHelperFile : mDialogHelperSms;
        TextView fieldnoteCaveat = ((TextView)dialog.findViewById(R.id.fieldnote_caveat));
        dialogHelper.configureDialogText(dialog, fieldnoteCaveat);
        EditText editText = ((EditText)dialog.findViewById(R.id.fieldnote));
        mDialogHelperCommon.configureDialogText(fieldnoteCaveat);

        dialogHelper.configureEditor(editText);
        mDialogHelperCommon.configureEditor(editText, localDate, dnf);
    }
}
