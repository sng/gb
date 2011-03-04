package com.google.code.geobeagle.activity.cachelist.presenter;

import android.app.ListActivity;

public interface ListFragtivityOnCreateHandler {
    void onCreateActivity(ListActivity listActivity,
            GeocacheListPresenter geocacheListPresenter);

    void onCreateFragment(GeocacheListPresenter geocacheListPresenter,
            Object listFragmentParam);
}