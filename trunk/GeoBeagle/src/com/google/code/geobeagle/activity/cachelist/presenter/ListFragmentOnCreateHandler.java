package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.inject.Inject;

import android.app.ListActivity;
import android.app.ListFragment;
import android.widget.ListView;

public class ListFragmentOnCreateHandler implements ListFragtivityOnCreateHandler {
    private final GeocacheListAdapter geocacheListAdapter;

    @Inject
    public ListFragmentOnCreateHandler(GeocacheListAdapter geocacheListAdapter) {
        this.geocacheListAdapter = geocacheListAdapter;
    }

    @Override
    public void onCreateActivity(ListActivity listActivity,
            GeocacheListPresenter geocacheListPresenter) {
    }

    @Override
    public void onCreateFragment(GeocacheListPresenter geocacheListPresenter,
            Object listFragmentParam) {
        ListFragment listFragment = (ListFragment)listFragmentParam;
        ListView listView = listFragment.getListView();
        geocacheListPresenter.setupListView(listView);
        listFragment.setListAdapter(geocacheListAdapter);
    }

}