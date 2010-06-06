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

package com.google.code.geobeagle.activity.cachelist.view;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.ListViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.DifficultyAndTerrainPainterAnnotation;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GeocacheSummaryRowInflater {
    private BearingFormatter mBearingFormatter;
    private final Provider<DistanceFormatter> mDistanceFormatterProvider;
    private final IconOverlayFactory mIconOverlayFactory;
    private final IconRenderer mIconRenderer;
    private final LayoutInflater mLayoutInflater;
    private final ListViewBitmapCopier mListViewBitmapCopier;
    private final NameFormatter mNameFormatter;

    @Inject
    public GeocacheSummaryRowInflater(Provider<DistanceFormatter> distanceFormatterProvider,
            LayoutInflater layoutInflater, BearingFormatter relativeBearingFormatter,
            @DifficultyAndTerrainPainterAnnotation IconRenderer iconRenderer,
            ListViewBitmapCopier listViewBitmapCopier, IconOverlayFactory iconOverlayFactory,
            NameFormatter nameFormatter) {
        mLayoutInflater = layoutInflater;
        mDistanceFormatterProvider = distanceFormatterProvider;
        mBearingFormatter = relativeBearingFormatter;
        mIconRenderer = iconRenderer;
        mListViewBitmapCopier = listViewBitmapCopier;
        mIconOverlayFactory = iconOverlayFactory;
        mNameFormatter = nameFormatter;
    }

    public View inflate(View convertView) {
        if (convertView != null)
            return convertView;

        // Log.d("GeoBeagle", "SummaryRow::inflate(" + convertView + ")");

        View view = mLayoutInflater.inflate(R.layout.cache_row, null);
        RowViews rowViews = new RowViews((TextView)view.findViewById(R.id.txt_gcattributes),
                ((TextView)view.findViewById(R.id.txt_cache)), ((TextView)view
                        .findViewById(R.id.distance)), ((ImageView)view
                        .findViewById(R.id.gc_row_icon)), ((TextView)view
                        .findViewById(R.id.txt_gcid)), mIconOverlayFactory, mNameFormatter);
        view.setTag(rowViews);
        return view;
    }

    public void setData(View view, GeocacheVector geocacheVector) {
        ((RowViews)view.getTag()).set(geocacheVector, mBearingFormatter, mDistanceFormatterProvider
                .get(), mListViewBitmapCopier, mIconRenderer);
    }
}
