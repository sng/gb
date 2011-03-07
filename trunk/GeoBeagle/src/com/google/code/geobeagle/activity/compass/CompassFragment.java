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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.compass.CompassActivityModule.ViewViewContainer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.ResourceImages;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.activity.GuiceActivity;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class CompassFragment extends Fragment {
    static int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("GeoBeagle", "CompassFragment::onCreateView: " + counter);
        View inflatedView = inflater.inflate(R.layout.compass, container, false);
        TextView details = (TextView)inflatedView.findViewById(R.id.gcname);
        Bundle arguments = getArguments();
        if (arguments != null) {
            CacheListActivity guiceActivity = (CacheListActivity)this.getActivity();
            Injector injector = guiceActivity.getInjector();

            RadarView radarView = (RadarView)inflatedView.findViewById(R.id.radarview);
            radarView.setUseImperial(false);
            radarView.setDistanceView((TextView)inflatedView.findViewById(R.id.radar_distance),
                    (TextView)inflatedView.findViewById(R.id.radar_bearing),
                    (TextView)inflatedView.findViewById(R.id.radar_accuracy));
            ImageView cacheTypeImageView = (ImageView)inflatedView.findViewById(R.id.gcicon);
            IconOverlayFactory iconOverlayFactory = injector.getInstance(IconOverlayFactory.class);
            IconRenderer iconRenderer = injector.getInstance(IconRenderer.class);
            MapViewBitmapCopier mapViewBitmapCopier = injector
                    .getInstance(MapViewBitmapCopier.class);
            DifficultyAndTerrainPainter difficultyAndTerrainPainter = injector
                    .getInstance(DifficultyAndTerrainPainter.class);
            RatingsArray ratingsArray = injector.getInstance(RatingsArray.class);
            Resources resources = injector.getInstance(Resources.class);
            NameFormatter nameFormatter = injector.getInstance(NameFormatter.class);
            ViewViewContainer viewViewContainer = new ViewViewContainer(inflatedView);
            AttributeViewer gcDifficulty = CompassActivityModule.getLabelledAttributeViewer(
                    viewViewContainer, resources, ratingsArray, new int[] {
                            R.drawable.ribbon_unselected_dark, R.drawable.ribbon_half_bright,
                            R.drawable.ribbon_selected_bright
                    }, R.id.gc_difficulty, R.id.gc_text_difficulty);

            AttributeViewer gcTerrain = CompassActivityModule.getLabelledAttributeViewer(
                    viewViewContainer, resources, ratingsArray, new int[] {
                            R.drawable.paw_unselected_dark, R.drawable.paw_half_light,
                            R.drawable.paw_selected_light
                    }, R.id.gc_terrain, R.id.gc_text_terrain);
            GeocacheFromParcelFactory geocacheFromParcelFactory = injector
                    .getInstance(GeocacheFromParcelFactory.class);
            Geocache geocache = geocacheFromParcelFactory.createFromBundle(arguments);
            details.setText(geocache.getName());
            final TextView textViewName = (TextView)inflatedView.findViewById(R.id.gcname);
            final NameViewer gcName = new NameViewer(textViewName, nameFormatter);
            final ResourceImages gcContainer = new ResourceImages(
                    (TextView)inflatedView.findViewById(R.id.gc_text_container),
                    (ImageView)inflatedView.findViewById(R.id.gccontainer),
                    Arrays.asList(GeocacheViewer.CONTAINER_IMAGES));

            GeocacheViewer geocacheViewer = new GeocacheViewer(radarView, guiceActivity, gcName,
                    cacheTypeImageView, gcDifficulty, gcTerrain, gcContainer, iconOverlayFactory,
                    mapViewBitmapCopier, iconRenderer, difficultyAndTerrainPainter);
            geocacheViewer.set(geocache);
        }
        return inflatedView;
    }
}
