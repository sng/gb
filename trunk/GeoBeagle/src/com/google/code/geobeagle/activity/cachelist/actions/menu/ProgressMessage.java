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

enum ProgressMessage {

    SET_MAX {
        @Override
        void act(ProgressDialog progressDialog, int arg1) {
            progressDialog.setMax(arg1);
        }
    },
    SET_PROGRESS {
        @Override
        void act(ProgressDialog progressDialog, int arg1) {
            progressDialog.setProgress(arg1);
        }
    },
    DONE {
        @Override
        void act(ProgressDialog progressDialog, int arg1) {
            progressDialog.dismiss();
        }
    },
    START {
        @Override
        void act(ProgressDialog progressDialog, int arg1) {
            progressDialog.show();
        }
    };

    abstract void act(ProgressDialog progressDialog, int arg1);

    public static ProgressMessage fromInt(Integer i) {
        return ProgressMessage.class.getEnumConstants()[i];
    }
}