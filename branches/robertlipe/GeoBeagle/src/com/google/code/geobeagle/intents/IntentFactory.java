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

package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.UriParser;

import android.content.Intent;

public class IntentFactory {
    private final UriParser mUriParser;

    public IntentFactory(UriParser uriParser) {
        mUriParser = uriParser;
    }

    public Intent createIntent(String action) {
        return new Intent(action);
    }

    public Intent createIntent(String action, String uri) {
        return new Intent(action, mUriParser.parse(uri));
    }

}
