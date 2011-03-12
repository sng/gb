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

package com.google.code.geobeagle.activity.cachelist.actions.context.delete;

import com.google.code.geobeagle.OnClickCancelListener;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ContextActionDeleteDialogHelper {
    private final ContextActionDelete contextActionDelete;
    private final OnClickOk onClickOk;

    @Inject
    public ContextActionDeleteDialogHelper(ContextActionDelete contextActionDelete,
            OnClickOk onClickOk) {
        this.contextActionDelete = contextActionDelete;
        this.onClickOk = onClickOk;
    }

    public Dialog onCreateDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View confirmDeleteCacheView = LayoutInflater.from(activity).inflate(
                R.layout.confirm_delete_cache, null);

        builder.setNegativeButton(R.string.confirm_delete_negative, new OnClickCancelListener());
        builder.setView(confirmDeleteCacheView);

        return builder.setPositiveButton(R.string.delete_cache, onClickOk).create();
    }

    public void onPrepareDialog(Dialog dialog) {
        CharSequence confirmDeleteTitle = contextActionDelete.getConfirmDeleteTitle();
        dialog.setTitle(confirmDeleteTitle);
        TextView textView = (TextView)dialog.findViewById(R.id.delete_cache);
        textView.setText(contextActionDelete.getConfirmDeleteBodyText());
    }
}
