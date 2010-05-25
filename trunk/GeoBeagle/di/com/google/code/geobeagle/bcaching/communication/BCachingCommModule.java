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

package com.google.code.geobeagle.bcaching.communication;

import com.google.code.geobeagle.bcaching.communication.BCachingList.BCachingListFactory;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.config.AbstractAndroidModule;

public class BCachingCommModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(BCachingListFactory.class).to(BCachingListFactoryImpl.class);
    }

    static class BCachingListFactoryImpl implements BCachingListFactory {
        public BCachingList create(String s) throws BCachingException {
            try {
                return new BCachingList(new BCachingJSONObject(new JSONObject(s)));
            } catch (JSONException e) {
                throw new BCachingException("Error parsing data from bcaching server: "
                        + e.getLocalizedMessage());
            }
        }
    }
}
