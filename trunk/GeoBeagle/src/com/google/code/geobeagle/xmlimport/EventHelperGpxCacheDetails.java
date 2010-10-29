
package com.google.code.geobeagle.xmlimport;

import com.google.inject.Inject;

public class EventHelperGpxCacheDetails extends EventHelperGpx {
    @Inject
    public EventHelperGpxCacheDetails(XmlPathBuilder xmlPathBuilder,
            CacheTagsToDetails cacheTagsToDetails) {
        super(xmlPathBuilder, new EventHandlerGpx(cacheTagsToDetails));
    }

}
