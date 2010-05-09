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

package com.google.code.geobeagle.activity.cachelist.actions.menu;


import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class ProgressHandler extends Handler {
    private final ProgressDialog progressDialog;

    ProgressHandler(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d("GeoBeagle", "getting message: " + msg);
        progressDialog.setTitle("Importing from BCaching site");
        ProgressMessage.fromInt(msg.what).act(progressDialog, msg.arg1);
    }
}