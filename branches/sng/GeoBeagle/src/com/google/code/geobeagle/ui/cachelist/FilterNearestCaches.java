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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.WhereFactory;
import com.google.code.geobeagle.io.CacheReader.WhereFactoryAllCaches;
import com.google.code.geobeagle.io.CacheReader.WhereFactoryNearestCaches;

class FilterNearestCaches {
    private boolean mIsFiltered = true;
    private final WhereFactory mWhereFactories[];

    FilterNearestCaches(WhereFactoryAllCaches whereFactoryAllCaches,
            WhereFactoryNearestCaches whereFactoryNearestCaches) {
        mWhereFactories = new WhereFactory[] {
                whereFactoryAllCaches, whereFactoryNearestCaches
        };
    }

    int getMenuString() {
        return mIsFiltered ? R.string.menu_show_all_caches : R.string.menu_show_nearest_caches;
    }

    int getTitleText() {
        return mIsFiltered ? R.string.cache_list_title : R.string.cache_list_title_all;
    }

    WhereFactory getWhereFactory() {
        return mWhereFactories[mIsFiltered ? 1 : 0];
    }

    void toggle() {
        mIsFiltered = !mIsFiltered;
    }
}
