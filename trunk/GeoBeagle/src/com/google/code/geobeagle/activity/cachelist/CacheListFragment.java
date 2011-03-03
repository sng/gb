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

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CacheListFragment extends ListFragment {
    public static final String[] TITLES = 
    {
            "Henry IV (1)",   
            "Henry V",
            "Henry VIII",       
            "Richard II",
            "Richard III",
            "Merchant of Venice",  
            "Othello",
            "King Lear"
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Populate list with our static array of titles.
//        setListAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, TITLES));
        getCacheListDelegate().onCreateFragment(this);
    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return getCacheListDelegate().onContextItemSelected(item)
//                || super.onContextItemSelected(item);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("GeoBeagle", "CacheListFragment::onCreateOptionsMenu");
//        menu.add(0, 0, 0, "hello");

//        getCacheListDelegate().onCreateOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cache_list, container, false);
    }
//
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        getCacheListDelegate().onListItemClick(position);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d("GeoBeagle", "CacheListFragment::onOptionsItemSelected: " + item);
//        return getCacheListDelegate().onOptionsItemSelected(item)
//                || super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onPause() {
//        getCacheListDelegate().onPause();
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        getCacheListDelegate().onResume();
//    }
//
    private CacheListDelegate getCacheListDelegate() {
        return ((CacheListActivity)getActivity()).getCacheListDelegate();
    }
}
