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

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;

import java.util.ArrayList;

class QueryManager {
    /**
     * We need to cache "needs loading" status because we might be zoomed out so
     * far we see no caches. In that situation, the compass will attempt to
     * refresh us every second, and we'll query the database over and over again
     * to learn that we have too many caches for the same set of points.
     */
    static class CachedNeedsLoading {
        private GeoPoint mOldBottomRight;
        private GeoPoint mOldTopLeft;

        public CachedNeedsLoading(GeoPoint topLeft, GeoPoint bottomRight) {
            mOldTopLeft = topLeft;
            mOldBottomRight = bottomRight;
        }

        boolean needsLoading(GeoPoint newTopLeft, GeoPoint newBottomRight) {
            if (mOldTopLeft.equals(newTopLeft) && mOldBottomRight.equals(newBottomRight))
                return false;
            mOldTopLeft = newTopLeft;
            mOldBottomRight = newBottomRight;
            return true;
        }
    }

    static class PeggedLoader {
        private final DbFrontend mDbFrontend;
        private final ArrayList<Geocache> mNullList;
        private final Toaster mToaster;
        private boolean mTooManyCaches;

        PeggedLoader(DbFrontend dbFrontend, ArrayList<Geocache> nullList, Toaster toaster) {
            mNullList = nullList;
            mDbFrontend = dbFrontend;
            mToaster = toaster;
            mTooManyCaches = false;
        }

        ArrayList<Geocache> load(int latMin, int lonMin, int latMax, int lonMax,
                WhereFactoryFixedArea where, int[] newBounds) {
            if (mDbFrontend.count(0, 0, where) > 1500) {
                latMin = latMax = lonMin = lonMax = 0;
                if (!mTooManyCaches) {
                    mToaster.showToast();
                    mTooManyCaches = true;
                }
                return mNullList;
            }
            mTooManyCaches = false;
            newBounds[0] = latMin;
            newBounds[1] = lonMin;
            newBounds[2] = latMax;
            newBounds[3] = lonMax;
            return mDbFrontend.loadCaches(0, 0, where);
        }
    }

    private final CachedNeedsLoading mCachedNeedsLoading;
    private int[] mLatLonMinMax; // i.e. latmin, lonmin, latmax, lonmax
    private final PeggedLoader mPeggedLoader;

    QueryManager(PeggedLoader peggedLoader, CachedNeedsLoading cachedNeedsLoading,
            int[] latLonMinMax) {
        mLatLonMinMax = latLonMinMax;
        mPeggedLoader = peggedLoader;
        mCachedNeedsLoading = cachedNeedsLoading;
    }

    ArrayList<Geocache> load(GeoPoint newTopLeft, GeoPoint newBottomRight) {
        // Expand the area by the resolution so we get complete patches for the
        // density map. This isn't needed for the pins overlay, but it doesn't
        // hurt either.
        final int lonMin = newTopLeft.getLongitudeE6()
                - DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final int latMax = newTopLeft.getLatitudeE6() + DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int latMin = newBottomRight.getLatitudeE6()
                - DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int lonMax = newBottomRight.getLongitudeE6()
                + DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final WhereFactoryFixedArea where = new WhereFactoryFixedArea((double)latMin / 1E6,
                (double)lonMin / 1E6, (double)latMax / 1E6, (double)lonMax / 1E6);

        ArrayList<Geocache> list = mPeggedLoader.load(latMin, lonMin, latMax, lonMax, where,
                mLatLonMinMax);
        return list;
    }

    boolean needsLoading(GeoPoint newTopLeft, GeoPoint newBottomRight) {
        return mCachedNeedsLoading.needsLoading(newTopLeft, newBottomRight)
                && (newTopLeft.getLatitudeE6() > mLatLonMinMax[2]
                        || newTopLeft.getLongitudeE6() < mLatLonMinMax[1]
                        || newBottomRight.getLatitudeE6() < mLatLonMinMax[0] || newBottomRight
                        .getLongitudeE6() > mLatLonMinMax[3]);
    }
}
