package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.database.UpdateFilterWorker.ApplyFilterProgressDialog;
import com.google.code.geobeagle.database.UpdateFilterWorker.ClearFilterProgressDialog;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.os.Handler;
import android.os.Message;

public class UpdateFilterHandler extends Handler {
    public static final int INCREMENT_APPLY_FILTER_PROGRESS = 3;
    public static final int SHOW_APPLY_FILTER_PROGRESS = 2;
    public static final int DISMISS_APPLY_FILTER_PROGRESS = 1;
    public static final int DISMISS_CLEAR_FILTER_PROGRESS = 4;
    private final Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider;
    private final Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider;
    private final CacheListRefresh cacheListRefresh;
    private final UpdateFlag updateFlag;

    @Inject
    UpdateFilterHandler(CacheListRefresh cacheListRefresh,
            UpdateFlag updateFlag,
            Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider,
            Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider) {
        this.clearFilterProgressDialogProvider = clearFilterProgressDialogProvider;
        this.applyFilterProgressDialogProvider = applyFilterProgressDialogProvider;
        this.cacheListRefresh = cacheListRefresh;
        this.updateFlag = updateFlag;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == DISMISS_CLEAR_FILTER_PROGRESS) {
            ClearFilterProgressDialog clearFilterProgressDialog = clearFilterProgressDialogProvider
                    .get();
            clearFilterProgressDialog.dismiss();
            updateFlag.setUpdatesEnabled(true);
            cacheListRefresh.forceRefresh();
        } else if (msg.what == DISMISS_APPLY_FILTER_PROGRESS) {
            ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                    .get();
            applyFilterProgressDialog.dismiss();
            updateFlag.setUpdatesEnabled(true);
            cacheListRefresh.forceRefresh();
        } else if (msg.what == SHOW_APPLY_FILTER_PROGRESS) {
            ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                    .get();
            ClearFilterProgressDialog clearFilterProgressDialog = clearFilterProgressDialogProvider
                    .get();
            clearFilterProgressDialog.dismiss();
            applyFilterProgressDialog.setMax(msg.arg1);
            applyFilterProgressDialog.show();
        } else if (msg.what == INCREMENT_APPLY_FILTER_PROGRESS) {
            ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                    .get();
            applyFilterProgressDialog.incrementProgressBy(1);
        }
    }
}