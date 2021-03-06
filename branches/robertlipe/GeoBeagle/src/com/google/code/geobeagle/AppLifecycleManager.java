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

package com.google.code.geobeagle;

import android.content.SharedPreferences;

public class AppLifecycleManager {
    private final LifecycleManager[] mLifecycleManagers;
    private final SharedPreferences mPreferences;

    public AppLifecycleManager(SharedPreferences preferences, LifecycleManager[] lifecycleManagers) {
        mLifecycleManagers = lifecycleManagers;
        mPreferences = preferences;
    }

    public void onPause() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        for (LifecycleManager lifecycleManager : mLifecycleManagers) {
            lifecycleManager.onPause(editor);
        }
        editor.commit();
    }

    public void onResume() {
        for (LifecycleManager lifecycleManager : mLifecycleManagers) {
            lifecycleManager.onResume(mPreferences);
        }
    }
}
