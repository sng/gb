
package com.google.code.geobeagle.xmlimport;

import android.util.Log;

import java.io.IOException;
import java.util.List;

public class EventHandlerComposite implements EventHandler {
    private final List<EventHandler> eventHandlers;

    EventHandlerComposite(List<EventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public void endTag(String name, String previousFullPath) throws IOException {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.endTag(name, previousFullPath);
        }
    }

    @Override
    public void open(String filename) {
        Log.d("GeoBeagle", "COMPOSITE OPENING: " + filename);

        for (EventHandler eventHandler : eventHandlers) {
            Log.d("GeoBeagle", "COMPOSITE OPENING: " + eventHandler);
            eventHandler.open(filename);
        }
    }

    @Override
    public void startTag(String name, String fullPath, XmlPullParserWrapper xmlPullParser)
            throws IOException {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.startTag(name, fullPath, xmlPullParser);
        }
    }

    @Override
    public boolean text(String fullPath, String text) throws IOException {
        boolean ret = true;
        for (EventHandler eventHandler : eventHandlers) {
            ret &= eventHandler.text(fullPath, text);
        }
        return ret;
    }

}
