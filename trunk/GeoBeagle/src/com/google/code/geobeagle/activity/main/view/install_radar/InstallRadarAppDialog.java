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

package com.google.code.geobeagle.activity.main.view.install_radar;

import com.google.code.geobeagle.OnClickListenerNOP;
import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

public class InstallRadarAppDialog {
    private final Builder dialogBuilder;
    private final OnClickListenerNOP onClickListenerNOP;
    private final InstallRadarOnClickListenerPositive onClickListenerPositive;

    @Inject
    InstallRadarAppDialog(AlertDialog.Builder dialogBuilder,
            InstallRadarOnClickListenerPositive onClickListenerPositive,
            OnClickListenerNOP onClickListenerNOP) {
        this.dialogBuilder = dialogBuilder;
        this.onClickListenerPositive = onClickListenerPositive;
        this.onClickListenerNOP = onClickListenerNOP;
    }

    public void showInstallRadarDialog() {
        dialogBuilder.setMessage(R.string.ask_install_radar_app);
        dialogBuilder.setPositiveButton(R.string.install_radar, onClickListenerPositive);
        dialogBuilder.setNegativeButton(R.string.cancel, onClickListenerNOP);
        dialogBuilder.create().show();
    }

}
