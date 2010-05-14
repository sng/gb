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

package com.google.code.geobeagle.bcaching;

import com.google.inject.Inject;



import java.util.Hashtable;

class BCachingListImporter {
    static final String MAX_COUNT = "50";

    private final BCachingListImportHelper bCachingListImportHelper;
    private final Hashtable<String, String> params;

    @Inject
    BCachingListImporter(Hashtable<String, String> params,
            BCachingListImportHelper bCachingListImportHelper) {
        this.params = params;
        this.bCachingListImportHelper = bCachingListImportHelper;
    }

    private BCachingList getCacheListHelper(String maxCount, String lastUpdate)
            throws BCachingException {
        params.put("maxcount", maxCount);
        params.put("since", lastUpdate);
        return bCachingListImportHelper.importList(params);
    }

    BCachingList getCacheList(int startAt, long lastUpdate) throws BCachingException {
        params.put("first", Integer.toString(startAt));
        return getCacheListHelper(MAX_COUNT, String.valueOf(lastUpdate));
    }

    int getTotalCount(String lastUpdate) throws BCachingException {
        params.remove("first");
        return getCacheListHelper("0", lastUpdate).getTotalCount();
    }
}
