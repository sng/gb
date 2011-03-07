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
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.compass.CompassActivityModule.GeocacheViewerFactory;
import com.google.code.geobeagle.activity.compass.CompassActivityModule.ViewViewContainer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.inject.Injector;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CompassFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.compass, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            CacheListActivity guiceActivity = (CacheListActivity)this.getActivity();
            Injector injector = guiceActivity.getInjector();
            GeocacheViewerFactory geocacheViewerFactory = injector.getInstance(GeocacheViewerFactory.class);
            GeocacheViewer geocacheViewer = geocacheViewerFactory.create(new ViewViewContainer(inflatedView));
            GeocacheFromParcelFactory geocacheFromParcelFactory = injector
                    .getInstance(GeocacheFromParcelFactory.class);
            Geocache geocache = geocacheFromParcelFactory.createFromBundle(arguments);
            geocacheViewer.set(geocache);
        }
        return inflatedView;
    }
}
