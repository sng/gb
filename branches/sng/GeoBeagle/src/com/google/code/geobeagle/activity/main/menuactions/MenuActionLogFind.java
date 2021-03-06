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

package com.google.code.geobeagle.activity.main.menuactions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;

import android.app.Activity;

public class MenuActionLogFind extends MenuActionBase {
    private final Activity mActivity;

    public MenuActionLogFind(Activity activity) {
        super(R.string.menu_log_find);
        mActivity = activity;
    }

    @Override
    public void act() {
        mActivity.showDialog(R.id.menu_log_find);
    }
}
