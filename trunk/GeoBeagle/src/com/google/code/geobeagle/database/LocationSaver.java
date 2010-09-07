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

import com.google.code.geobeagle.Geocache;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LocationSaver {
    private final Provider<CacheWriter> cacheWriterProvider;
    private final TagWriterImpl tagWriterImpl;
    private final Filter filter;

    @Inject
    public LocationSaver(Provider<CacheWriter> cacheWriterProvider,
            TagWriterImpl tagWriterImpl,
            Filter filter) {
        this.cacheWriterProvider = cacheWriterProvider;
        this.tagWriterImpl = tagWriterImpl;
        this.filter = filter;
    }

    public void saveLocation(Geocache geocache) {
        CharSequence id = geocache.getId();
        CacheWriter cacheWriter = cacheWriterProvider.get();
        cacheWriter.startWriting();
        boolean found = tagWriterImpl.hasTag(id, Tag.FOUND);
        boolean visible = filter.isVisible(found);
        cacheWriter.insertAndUpdateCache(id, geocache.getName(), geocache.getLatitude(),
                geocache.getLongitude(), geocache.getSourceType(), geocache.getSourceName(),
                geocache.getCacheType(), geocache.getDifficulty(), geocache.getTerrain(),
                geocache.getContainer(), geocache.getAvailable(), geocache.getArchived(), visible);
        cacheWriter.stopWriting();
    }
}
