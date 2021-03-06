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

package com.google.code.geobeagle.bcaching;

import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import android.app.ProgressDialog;
import android.content.Context;

@ContextScoped
public class BCachingProgressDialog extends ProgressDialog {
    @Inject
    public BCachingProgressDialog(Context context) {
        super(context);
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setTitle("Sync from BCaching.com");
        setMessage(BCachingModule.BCACHING_INITIAL_MESSAGE);
        setCancelable(false);
    }
}