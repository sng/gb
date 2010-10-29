package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;

class EventHelperCacheUri extends EventHelper {

    public EventHelperCacheUri(XmlPathBuilder xmlPathBuilder, CacheTagsToUrl cacheTagsToUrl) {
        super(xmlPathBuilder, new EventHandlerGpx(cacheTagsToUrl));
    }

}