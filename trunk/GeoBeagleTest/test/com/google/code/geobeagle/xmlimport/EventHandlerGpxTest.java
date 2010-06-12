/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.anyObject;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class EventHandlerGpxTest extends GeoBeagleTest {

    private CachePersisterFacade mCachePersisterFacade;
    private XmlWriter xmlWriter;

    @Before
    public void setUp() {
        mCachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter = createMock(XmlWriter.class);
    }

    @Test
    public void testDesc() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.wptDesc("a cache");
        cachePersisterFacade.line("a cache");
        xmlWriter.text(EventHandlerGpx.XPATH_WPTDESC, "a cache");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_WPTDESC,
                "a cache");
        verifyAll();
    }

    @Test
    public void testEndTag() throws IOException {
        mCachePersisterFacade.endCache(Source.GPX);
        xmlWriter.endTag("wpt", "/gpx/wpt");

        replayAll();
        new EventHandlerGpx(mCachePersisterFacade, xmlWriter).endTag("wpt", "/gpx/wpt");
        verifyAll();
    }

    @Test
    public void testGpxTime() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        xmlWriter.text(EventHandlerGpx.XPATH_GPXTIME, "today");
        expect(cachePersisterFacade.gpxTime("today")).andReturn(true);

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_GPXTIME,
                "today");
        verifyAll();
    }

    @Test
    public void testGroundspeakName() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text(EventHandlerGpx.XPATH_GROUNDSPEAKNAME, "my wpt");
        cachePersisterFacade.groundspeakName("my wpt");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(
                EventHandlerGpx.XPATH_GROUNDSPEAKNAME, "my wpt");
        verifyAll();
    }

    @Test
    public void testHint() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text(EventHandlerGpx.XPATH_HINT, " look under the rock");

        cachePersisterFacade.hint("look under the rock");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_HINT,
                " look under the rock");
        verifyAll();
    }

    @Test
    public void testHintEmpty() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, null).text(EventHandlerGpx.XPATH_HINT, "   ");
        verifyAll();
    }

    @Test
    public void testLogDate() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        xmlWriter.text(EventHandlerGpx.XPATH_LOGDATE, "date");

        cachePersisterFacade.logDate("date");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_LOGDATE,
                "date");
        verifyAll();
    }

    @Test
    public void testMatchNothing() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text("/gpx/foo", "hello");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text("/gpx/foo", "hello");
        verifyAll();
    }

    @Test
    public void testPlainLine() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text(EventHandlerGpx.XPATH_PLAINLINES[0], " hello  \t");

        cachePersisterFacade.line("hello");

        replayAll();
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade, xmlWriter);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_PLAINLINES[0], " hello  \t");
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStartTagCache() throws IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        expect(xmlPullParser.getAttributeCount()).andReturn(2);
        cachePersisterFacade.startCache();
        expect(xmlPullParser.getAttributeName(0)).andReturn("lat");
        expect(xmlPullParser.getAttributeValue(0)).andReturn("37");
        expect(xmlPullParser.getAttributeName(1)).andReturn("lon");
        expect(xmlPullParser.getAttributeValue(1)).andReturn("122");
        xmlWriter.startTag(eq("wpt"), (HashMap<String, String>)anyObject());
        cachePersisterFacade.wpt("37", "122");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).startTag("wpt", "/gpx/wpt",
                xmlPullParser);
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAvailable() throws IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        expect(xmlPullParser.getAttributeCount()).andReturn(2);
        expect(xmlPullParser.getAttributeName(0)).andReturn("available");
        expect(xmlPullParser.getAttributeValue(0)).andReturn("true");
        expect(xmlPullParser.getAttributeName(1)).andReturn("archived");
        expect(xmlPullParser.getAttributeValue(1)).andReturn("false");
        xmlWriter.startTag(eq("groundspeak:cache"), (HashMap<String, String>)anyObject());
        cachePersisterFacade.available("true");
        cachePersisterFacade.archived("false");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).startTag("groundspeak:cache",
                "/gpx/wpt/groundspeak:cache", xmlPullParser);
        verifyAll();
    }

    @Test
    public void testStartTagNotCache() throws IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        expect(xmlPullParser.getAttributeCount()).andReturn(0);

        new EventHandlerGpx(null, xmlWriter).startTag("wptNot", "/gpx/wptNot", xmlPullParser);
    }

    @Test
    public void testTextSymbol() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        xmlWriter.text(EventHandlerGpx.XPATH_SYM, "Geocache Found");

        cachePersisterFacade.symbol("Geocache Found");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_SYM,
                "Geocache Found");
        verifyAll();
    }

    @Test
    public void testTextCacheType() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text(EventHandlerGpx.XPATH_CACHE_TYPE, "cache type");
        xmlWriter.text(EventHandlerGpx.XPATH_CACHE_DIFFICULTY, "difficulty");
        xmlWriter.text(EventHandlerGpx.XPATH_CACHE_TERRAIN, "terrain");
        xmlWriter.text(EventHandlerGpx.XPATH_CACHE_CONTAINER, "container");

        cachePersisterFacade.cacheType("cache type");
        cachePersisterFacade.line("cache type");
        cachePersisterFacade.difficulty("difficulty");
        cachePersisterFacade.terrain("terrain");
        cachePersisterFacade.container("container");
        cachePersisterFacade.line("container");

        replayAll();
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade, xmlWriter);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_TYPE, "cache type");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_DIFFICULTY, "difficulty");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_TERRAIN, "terrain");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_CONTAINER, "container");
        verifyAll();
    }

    @Test
    public void testTextWptName() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlWriter.text(EventHandlerGpx.XPATH_WPTNAME, "my wpt");

        cachePersisterFacade.wptName("my wpt");

        replayAll();
        new EventHandlerGpx(cachePersisterFacade, xmlWriter).text(EventHandlerGpx.XPATH_WPTNAME,
                "my wpt");
        verifyAll();
    }
}
