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

import android.util.Log;

import java.util.Calendar;

public class Timing {
    private long mStartTime;

    public void lap(CharSequence msg) {
        long finishTime = Calendar.getInstance().getTimeInMillis();
        Log.d("GeoBeagle", "****** " + msg + ": " + finishTime + ": " + (finishTime - mStartTime));
        mStartTime = finishTime;
    }

    public void start() {
        mStartTime = Calendar.getInstance().getTimeInMillis();
    }

    public long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
