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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.compass.CompassFragment;
import com.google.inject.Injector;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class CacheListFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCacheListDelegate().onCreateFragment(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cache_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        CompassFragment compassFragment = new CompassFragment();
        Bundle bundle = new Bundle();
        GeocacheVector geocacheVector = getInjector().getInstance(GeocacheVectors.class).get(position - 1);
        geocacheVector.getGeocache().saveToBundle(bundle);
    
        compassFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        Log.d("GeoBeagle",
                "CacheListFragment find compass: "
                        + fragmentManager.findFragmentById(R.id.compass_frame));
        Log.d("GeoBeagle", "CacheListFragment new compass: " + compassFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.compass_frame, compassFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        // getCacheListDelegate().onListItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getCacheListDelegate().onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    private Injector getInjector() {
        return ((CacheListActivity)getActivity()).getInjector();
    }
    private CacheListDelegate getCacheListDelegate() {
        return ((CacheListActivity)getActivity()).getCacheListDelegate();
    }
}
