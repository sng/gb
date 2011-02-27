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

import com.google.code.geobeagle.R;
import com.google.inject.Injector;

import roboguice.activity.GuiceActivity;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CacheListActivityHoneycomb extends GuiceActivity {
    public static class CacheListFragment extends ListFragment {

        private CacheListDelegate mCacheListDelegate;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mCacheListDelegate.onCreateFragment(this);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            CacheListActivityHoneycomb cacheListActivity = (CacheListActivityHoneycomb)activity;
            Injector injector = cacheListActivity.getInjector();
            mCacheListDelegate = injector.getInstance(CacheListDelegate.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.cache_list, container, false);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            mCacheListDelegate.onCreateOptionsMenu(menu);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            mCacheListDelegate.onListItemClick(position);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return mCacheListDelegate.onOptionsItemSelected(item)
                    || super.onOptionsItemSelected(item);
        }

        @Override
        public void onPause() {
            mCacheListDelegate.onPause();
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
            mCacheListDelegate.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            CacheListFragment cacheListFragment = new CacheListFragment();
            cacheListFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, cacheListFragment)
                    .commit();
        }
    }

}
