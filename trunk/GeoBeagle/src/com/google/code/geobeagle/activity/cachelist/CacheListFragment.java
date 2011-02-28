
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
import android.widget.ListView;

public class CacheListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCacheListDelegate().onCreateFragment(this);
    }

    private CacheListDelegate getCacheListDelegate() {
        return ((CacheListActivity)getActivity()).getCacheListDelegate();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return getCacheListDelegate().onContextItemSelected(item)
                || super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cache_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("GeoBeagle", "CacheListFragment::onCreateOptionsMenu");
        getCacheListDelegate().onCreateOptionsMenu(menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getCacheListDelegate().onListItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("GeoBeagle", "CacheListFragment::onOptionsItemSelected: " + item);
        return getCacheListDelegate().onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        getCacheListDelegate().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCacheListDelegate().onResume();
    }
}
