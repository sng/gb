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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.intents.IntentStarter;

import android.app.Activity;
import android.widget.Button;

public class OnCacheButtonClickListenerBuilder {
    private final Activity mContext;
    private final ErrorDisplayer mErrorDisplayer;

    public OnCacheButtonClickListenerBuilder(Activity context, ErrorDisplayer errorDisplayer) {
        mErrorDisplayer = errorDisplayer;
        mContext = context;
    }

    public void set(int id, IntentStarter intentStarter, String errorString) {
        ((Button)mContext.findViewById(id)).setOnClickListener(new CacheButtonOnClickListener(
                intentStarter, mErrorDisplayer, errorString));
    }
}
