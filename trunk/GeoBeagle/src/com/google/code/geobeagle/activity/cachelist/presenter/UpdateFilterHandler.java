
package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.database.UpdateFilterWorker.ApplyFilterProgressDialog;
import com.google.code.geobeagle.database.UpdateFilterWorker.ClearFilterProgressDialog;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.os.Handler;
import android.os.Message;

public class UpdateFilterHandler extends Handler {
    private final UpdateFilterMediator updateFilterMediator;

    public static enum UpdateFilterMessages {

        DISMISS_CLEAR_FILTER_PROGRESS {
            @Override
            void handleMessage(UpdateFilterMediator updateFilterMediator, int arg1) {
                updateFilterMediator.dismissClearFilterProgress();
            }
        },
        DISMISS_APPLY_FILTER_PROGRESS {
            @Override
            void handleMessage(UpdateFilterMediator updateFilterMediator, int arg1) {
                updateFilterMediator.dismissApplyFilterProgress();
            }
        },
        SHOW_APPLY_FILTER_PROGRESS {
            @Override
            void handleMessage(UpdateFilterMediator updateFilterMediator, int arg1) {
                updateFilterMediator.showApplyFilterProgress(arg1);
            }
        },
        INCREMENT_APPLY_FILTER_PROGRESS {
            @Override
            void handleMessage(UpdateFilterMediator updateFilterMediator, int arg1) {
                updateFilterMediator.incrementApplyFilterProgress();
            }
        };
        abstract void handleMessage(UpdateFilterMediator updateFilterMediator, int arg1);

        public static UpdateFilterMessages fromOrd(int i) {
            return UpdateFilterMessages.values()[i];
        }
    }

    @Inject
    UpdateFilterHandler(UpdateFilterMediator updateFilterMediator) {
        this.updateFilterMediator = updateFilterMediator;
    }

    static class UpdateFilterMediator {
        public UpdateFilterMediator(CacheListRefresh cacheListRefresh,
                UpdateFlag updateFlag,
                Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider,
                Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider) {
            this.cacheListRefresh = cacheListRefresh;
            this.updateFlag = updateFlag;
            this.applyFilterProgressDialogProvider = applyFilterProgressDialogProvider;
            this.clearFilterProgressDialogProvider = clearFilterProgressDialogProvider;
        }

        private final Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider;
        private final Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider;
        private final CacheListRefresh cacheListRefresh;
        private final UpdateFlag updateFlag;

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

    }

    @Override
    public void handleMessage(Message msg) {
        UpdateFilterMessages.fromOrd(msg.what).handleMessage(updateFilterMediator, msg.arg1);
    }
}
