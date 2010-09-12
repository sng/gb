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

package com.google.code.geobeagle.database;

import com.google.inject.Inject;

import android.util.Log;

public class TagWriter {
    private final Filter filter;
    private final TagStore tagStore;

    @Inject
    public TagWriter(
            Filter filter,
            TagStore tagStore) {
        this.filter = filter;
        this.tagStore = tagStore;
    }

    public void add(CharSequence geocacheId, Tag tag) {
        Log.d("GeoBeagle", "TagWriter: " + geocacheId + ", " + tag);
        tagStore.addTag(geocacheId, tag);

        if (!filter.isVisible(tag == Tag.FOUND)) {
            tagStore.hideCache(geocacheId);
        }
    }

    public boolean hasTag(CharSequence geocacheId, Tag tag) {
        return tagStore.hasTag(geocacheId, tag);
    }

}