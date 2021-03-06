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

package com.google.code.geobeagle.activity.map;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.inject.Inject;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

class CacheItemFactory {
    private final IconRenderer mIconRenderer;
    private final MapViewBitmapCopier mMapViewBitmapCopier;
    private final IconOverlayFactory mIconOverlayFactory;
    private final DifficultyAndTerrainPainter mDifficultyAndTerrainPainter;

    @Inject
    CacheItemFactory(MapViewBitmapCopier mapViewBitmapCopier,
            IconOverlayFactory iconOverlayFactory, Resources resources,
            DifficultyAndTerrainPainter difficultyAndTerrainPainter) {
        mIconRenderer = new IconRenderer(resources);
        mMapViewBitmapCopier = mapViewBitmapCopier;
        mIconOverlayFactory = iconOverlayFactory;
        mDifficultyAndTerrainPainter = difficultyAndTerrainPainter;
    }

    CacheItem createCacheItem(Geocache geocache) {
        final CacheItem cacheItem = new CacheItem(geocache.getGeoPoint(), (String)geocache.getId(),
                geocache);

        final Drawable icon = mIconRenderer.renderIcon(geocache.getDifficulty(), geocache
                .getTerrain(), geocache.getCacheType().iconMap(), mIconOverlayFactory.create(
                geocache, false), mMapViewBitmapCopier, mDifficultyAndTerrainPainter);
        cacheItem.setMarker(icon);
        return cacheItem;
    }
}
