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

package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.CompassFragment;
import com.google.code.geobeagle.activity.compass.fieldnotes.CacheLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.inject.Inject;

import android.app.Activity;
import android.widget.EditText;

public class OnClickOkFactory {
    private final CacheLogger cacheLogger;
    private final Activity activity;

    @Inject
    public OnClickOkFactory(Activity activity, CacheLogger cacheLogger) {
        this.activity = activity;
        this.cacheLogger = cacheLogger;
    }

    public OnClickOk create(EditText editText, boolean dnf) {
        CompassFragment fragment = (CompassFragment)activity.getFragmentManager().findFragmentById(
                R.id.compass_frame);

        return new OnClickOk(fragment.getGeocache().getId(), editText, cacheLogger, dnf);
    }
}
