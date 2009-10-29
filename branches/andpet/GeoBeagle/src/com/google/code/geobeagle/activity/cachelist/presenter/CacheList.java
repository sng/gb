package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.DistanceAndBearing;
import com.google.code.geobeagle.database.DistanceAndBearing.IDistanceAndBearingProvider;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

//TODO: Rename this class
/** Feeds the caches in a CachesProvider to the GUI list view */
public class CacheList extends BaseAdapter implements Refresher {
    private final CachesProviderToggler mProvider;
    private final IDistanceAndBearingProvider mDistances;
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;
    private final TitleUpdater mTitleUpdater;
    private ArrayList<Geocache> mListData;
    private float mAzimuth;
    private boolean mUpdatesEnabled = true;

    public CacheList(CachesProviderToggler provider, 
            IDistanceAndBearingProvider distances,
            GeocacheSummaryRowInflater inflater,
            TitleUpdater titleUpdater) {
        mProvider = provider;
        mGeocacheSummaryRowInflater = inflater;
        mTitleUpdater = titleUpdater;
        mDistances = distances;
    }
    
    public void enableUpdates(boolean enable) {
        mUpdatesEnabled = enable;
        refresh();
    }
    
    public void setAzimuth (float azimuth) {
        mAzimuth = azimuth;
    }
    
    /** Updates the GUI from the Provider if necessary */
    @Override
    public void refresh() {
        if (!mUpdatesEnabled || !mProvider.hasChanged()) {
            return;
        }

        forceRefresh();
    }

    public void forceRefresh() {
        //TODO: Take this back?
        //mProvider.resetChanged();
        mListData = mProvider.getCaches();
        mTitleUpdater.refresh();
        notifyDataSetChanged();
    }

    //TODO: Cache the list in this class to avoid partial updates and such
    public int getCount() {
        if (mListData == null)
            return 0;
        return mListData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /** Get the geocache for a certain row in the displayed list, starting with zero */
    public Geocache getGeocacheAt(int position) {
        if (mListData == null)
            return null;
        Geocache cache = mListData.get(position);
        return cache;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mListData == null)
            return null; //What happens in this case?
        View view = mGeocacheSummaryRowInflater.inflate(convertView);
        Geocache cache = mListData.get(position);
        DistanceAndBearing geocacheVector = mDistances.getDistanceAndBearing(cache);
        mGeocacheSummaryRowInflater.setData(view, geocacheVector, mAzimuth);
        return view;
    }
    
}
