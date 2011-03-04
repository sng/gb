package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.app.ListActivity;
import android.widget.ListView;

public class ListActivityOnCreateHandler implements ListFragtivityOnCreateHandler {
    private final GeocacheListAdapter geocacheListAdapter;

    @Inject
    public ListActivityOnCreateHandler(GeocacheListAdapter geocacheListAdapter) {
        this.geocacheListAdapter = geocacheListAdapter;
    }

    @Override
    public void onCreateActivity(ListActivity listActivity,
            GeocacheListPresenter geocacheListPresenter) {
        listActivity.setContentView(R.layout.cache_list);
        ListView listView = listActivity.getListView();
        geocacheListPresenter.setupListView(listView);
        listActivity.setListAdapter(geocacheListAdapter);
    }

    @Override
    public void onCreateFragment(GeocacheListPresenter geocacheListPresenter,
            Object listFragmentParam) {
    }
}