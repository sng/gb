package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.AbstractList;

//TODO: Remove this class and replace by a mechanism to change CachesProvider in CacheListAdapter
public class CachesProviderToggler implements ICachesProviderCenter {
    private ICachesProviderCenter mCachesProviderCenter;
    private ICachesProvider mCachesProviderAll;
    private boolean mNearest;
    private boolean mHasChanged;

    public CachesProviderToggler(ICachesProviderCenter cachesProviderCenter,
            ICachesProvider cachesProviderAll) {
        mCachesProviderCenter = cachesProviderCenter;
        mCachesProviderAll = cachesProviderAll;
        mNearest = true;
        mHasChanged = true;
    }

    public void toggle() {
        mNearest = !mNearest;
        mHasChanged = true;
    }

    public boolean isShowingNearest() {
        return mNearest;
    }
    
    @Override
    public AbstractList<Geocache> getCaches() {
        return (mNearest ? mCachesProviderCenter : mCachesProviderAll).getCaches();
    }

    @Override
    public int getCount() {
        return (mNearest ? mCachesProviderCenter : mCachesProviderAll).getCount();
    }

    @Override
    public void setCenter(double latitude, double longitude) {
        mCachesProviderCenter.setCenter(latitude, longitude);
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || 
            (mNearest ? mCachesProviderCenter : mCachesProviderAll).hasChanged();
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
        (mNearest ? mCachesProviderCenter : mCachesProviderAll).resetChanged();
    }
}
