package com.google.code.geobeagle;

import java.util.Hashtable;

public class CacheTypeFactory {
    private final Hashtable<Integer, CacheType> mCacheTypes = 
        new Hashtable<Integer, CacheType>(CacheType.values().length);

    public CacheTypeFactory() {
        for (CacheType cacheType : CacheType.values())
            mCacheTypes.put(cacheType.toInt(), cacheType);
    }

    public CacheType fromInt(int i) {
        if (!mCacheTypes.containsKey(i))
            return CacheType.NULL;
        return mCacheTypes.get(i);
    }

    public CacheType fromTag(String tag) {
        String tagLower = tag.toLowerCase();
        int longestMatch = 0;

        CacheType result = CacheType.NULL;
        for (CacheType cacheType : mCacheTypes.values()) {
            String cacheTypeTag = cacheType.getTag();
            if (tagLower.contains(cacheTypeTag) && cacheTypeTag.length() > longestMatch) {
                result = cacheType;
                // Necessary to continue the search to find mega-events and
                // individual waypoint types.
                longestMatch = cacheTypeTag.length();
            }
        }

        return result;
    }

    public int container(String container) {
        if (container.equals("Micro")) {
            return 1;
        } else if (container.equals("Small")) {
            return 2;
        } else if (container.equals("Regular")) {
            return 3;
        } else if (container.equals("Large")) {
            return 4;
        }
        return 0;
    }

    public int stars(String stars) {
        try {
            return Math.round(Float.parseFloat(stars) * 2);
        } catch (Exception ex) {
            return 0;
        }
    }
}