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

package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.shakewaker.ShakeWaker;

import roboguice.activity.GuiceActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class DetailsActivity extends GuiceActivity {

    private DetailsWebView detailsWebView;
    private ShakeWaker shakeWaker;
    public static final String INTENT_EXTRA_GEOCACHE_SOURCE = "geocache_source";
    public static final String INTENT_EXTRA_GEOCACHE_ID = "geocache_id";
    public static final String INTENT_EXTRA_GEOCACHE_NAME = "geocache_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        shakeWaker = getInjector().getInstance(ShakeWaker.class);
        setTitle(detailsWebView.loadDetails((WebView)findViewById(R.id.cache_details), getIntent()));
    }

    public DetailsActivity(ShakeWaker shakeWaker) {
        this.shakeWaker = shakeWaker;
    }

    @Override
    public void onPause() {
        shakeWaker.unregister();
    }

    @Override
    public void onResume() {
        shakeWaker.register();
    }

}
