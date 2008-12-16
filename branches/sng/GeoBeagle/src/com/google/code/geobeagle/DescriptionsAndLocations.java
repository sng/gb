
package com.google.code.geobeagle;

import java.util.ArrayList;
import java.util.List;

public class DescriptionsAndLocations {
    private List<CharSequence> mPreviousDescriptions;

    private List<CharSequence> mPreviousLocations;

    private int mMaxSize;

    public DescriptionsAndLocations() {
        create(25);
    }

    public DescriptionsAndLocations(int maxSize) {
        create(maxSize);
    }

    private void create(int maxSize) {
        mPreviousDescriptions = new ArrayList<CharSequence>();
        mPreviousLocations = new ArrayList<CharSequence>();
        this.mMaxSize = maxSize;
    }

    public void add(CharSequence description, CharSequence location) {
        final int ix = mPreviousDescriptions.indexOf(description);
        if (ix >= 0) {
            remove(ix);
        }

        mPreviousDescriptions.add(description);
        mPreviousLocations.add(location);
        if (mPreviousDescriptions.size() > mMaxSize) {
            remove(0);
        }
    }

    public void clear() {
        mPreviousDescriptions.clear();
        mPreviousLocations.clear();
    }

    public List<CharSequence> getPreviousDescriptions() {
        return mPreviousDescriptions;
    }

    public List<CharSequence> getPreviousLocations() {
        return mPreviousLocations;
    }

    private void remove(int i) {
        mPreviousLocations.remove(i);
        mPreviousDescriptions.remove(i);
    }
}
