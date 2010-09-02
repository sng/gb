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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.database.ApplyFilterProgressDialog;
import com.google.code.geobeagle.database.ClearFilterProgressDialog;
import com.google.inject.Provider;

class UpdateFilterMediator {
    private final Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider;
    private final CacheListRefresh cacheListRefresh;
    private final Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider;
    private final UpdateFlag updateFlag;

    public UpdateFilterMediator(CacheListRefresh cacheListRefresh,
            UpdateFlag updateFlag,
            Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider,
            Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider) {
        this.cacheListRefresh = cacheListRefresh;
        this.updateFlag = updateFlag;
        this.applyFilterProgressDialogProvider = applyFilterProgressDialogProvider;
        this.clearFilterProgressDialogProvider = clearFilterProgressDialogProvider;
    }

    void dismissApplyFilterProgress() {
        ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                .get();
        applyFilterProgressDialog.dismiss();
        updateFlag.setUpdatesEnabled(true);
        cacheListRefresh.forceRefresh();
    }

    void dismissClearFilterProgress() {
        ClearFilterProgressDialog clearFilterProgressDialog = clearFilterProgressDialogProvider
                .get();
        clearFilterProgressDialog.dismiss();
        updateFlag.setUpdatesEnabled(true);
        cacheListRefresh.forceRefresh();
    }

    void incrementApplyFilterProgress() {
        ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                .get();
        applyFilterProgressDialog.incrementProgressBy(1);
    }

    void showApplyFilterProgress(int arg1) {
        ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                .get();
        ClearFilterProgressDialog clearFilterProgressDialog = clearFilterProgressDialogProvider
                .get();
        clearFilterProgressDialog.dismiss();
        applyFilterProgressDialog.setMax(arg1);
        applyFilterProgressDialog.show();
    }

}