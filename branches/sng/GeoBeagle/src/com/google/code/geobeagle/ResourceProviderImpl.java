
package com.google.code.geobeagle;

import android.content.Context;

public class ResourceProviderImpl implements ResourceProvider {

    private final Context mContext;

    public ResourceProviderImpl(Context context) {
        mContext = context;
    }

    public String getString(int resourceId) {
        return mContext.getString(resourceId);
    }

}
