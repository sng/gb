package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenterHoneycomb;
import com.google.code.geobeagle.activity.cachelist.presenter.ListFragtivity;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Injector;
import com.google.inject.Provider;

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

public class CacheListFragment extends ListFragment implements ListFragtivity {

    private ActivityVisible activityVisible;
    private CacheListRefresh cacheListRefresh;
    private GeocacheListController geocacheListController;
    private GeocacheListPresenterHoneycomb geocacheListPresenter;
    private ActivitySaver activitySaver;
    private Provider<DbFrontend> dbFrontendProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        geocacheListPresenter.onCreateFragment(geocacheListPresenter, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        CacheListActivityHoneycomb cacheListActivity = (CacheListActivityHoneycomb)activity;
        Injector injector = cacheListActivity.getInjector();
        this.geocacheListPresenter = injector.getInstance(GeocacheListPresenterHoneycomb.class);
        this.geocacheListController = injector.getInstance(GeocacheListController.class);
        this.cacheListRefresh = injector.getInstance(CacheListRefresh.class);
        this.activityVisible = injector.getInstance(ActivityVisible.class);
        this.activitySaver = injector.getInstance(ActivitySaver.class);
        this.dbFrontendProvider = injector.getProvider(DbFrontend.class);
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
        geocacheListController.onCreateOptionsMenu(menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        geocacheListController.onListItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return geocacheListController.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        activityVisible.setVisible(false);
        geocacheListPresenter.onPause();
        geocacheListController.onPause();
        activitySaver.save(ActivityType.CACHE_LIST);
        dbFrontendProvider.get().closeDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityVisible.setVisible(true);
        geocacheListPresenter.onResume(cacheListRefresh);
        geocacheListController.onResume(false);
    }

    @Override
    public void setContentView(int cacheList) {
    }
}