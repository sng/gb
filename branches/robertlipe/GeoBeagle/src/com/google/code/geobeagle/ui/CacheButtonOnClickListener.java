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

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class CacheButtonOnClickListener implements OnClickListener {
    private final IntentStarter mDestinationToIntentFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final String mErrorMessage;

    public CacheButtonOnClickListener(IntentStarter intentStarter, ErrorDisplayer errorDisplayer,
            String errorMessage) {
        mDestinationToIntentFactory = intentStarter;
        mErrorDisplayer = errorDisplayer;
        mErrorMessage = errorMessage;
    }

    public void onClick(View view) {
        try {
            mDestinationToIntentFactory.startIntent();
        } catch (final ActivityNotFoundException e) {
            mErrorDisplayer.displayError("Error: " + e.getMessage() + mErrorMessage);
        } catch (final Exception e) {
            mErrorDisplayer.displayError("Error: " + e.getMessage());
        }
    }
}
