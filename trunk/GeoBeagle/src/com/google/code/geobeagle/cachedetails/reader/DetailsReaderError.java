package com.google.code.geobeagle.cachedetails.reader;

import com.google.code.geobeagle.xmlimport.ICachePersisterFacade;

import android.app.Activity;

public class DetailsReaderError implements DetailsReader {
    private final Activity mActivity;
    private final int mError;
    private final String mPath;

    public DetailsReaderError(Activity activity, int error, String path) {
        mActivity = activity;
        mPath = path;
        mError = error;
    }

    @Override
    public String read(ICachePersisterFacade cpf) {
        return mActivity.getString(mError, mPath);
    }
}