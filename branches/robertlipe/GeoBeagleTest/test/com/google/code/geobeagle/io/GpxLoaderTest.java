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

package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.GpxToCache.CancelException;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

import junit.framework.TestCase;

public class GpxLoaderTest extends TestCase {

    private <T> void loadRaiseAndDisplayCustomMessage(Class<T> exceptionClass, int errorResource)
            throws XmlPullParserException, IOException, ParseException, CancelException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Throwable e = (Throwable)createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        expect(gpxToCache.load()).andStubReturn(false);
        expectLastCall().andThrow(e);
        expect(gpxToCache.getSource()).andReturn("foo.gpx");
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayError(errorResource, "foo.gpx");
        cachePersisterFacade.close(false);

        replay(e);
        replay(errorDisplayer);
        replay(cachePersisterFacade);
        replay(gpxToCache);
        assertFalse(new GpxLoader(gpxToCache, cachePersisterFacade, errorDisplayer).load());
        verify(cachePersisterFacade);
        verify(gpxToCache);
        verify(errorDisplayer);
    }

    private <T> void loadRaiseAndDisplayExceptionMessage(int errorMessage, Class<T> exceptionClass)
            throws XmlPullParserException, IOException, ParseException, CancelException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Throwable e = (Throwable)createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        gpxToCache.load();
        expectLastCall().andThrow(e);
        expect(e.getMessage()).andReturn("a problem of some sort");
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayError(errorMessage, "a problem of some sort");
        cachePersisterFacade.close(false);

        replay(e);
        replay(errorDisplayer);
        replay(cachePersisterFacade);
        replay(gpxToCache);
        assertFalse(new GpxLoader(gpxToCache, cachePersisterFacade, errorDisplayer).load());
        verify(cachePersisterFacade);
        verify(gpxToCache);
        verify(errorDisplayer);
    }

    private <T> void loadRaiseAndDisplayNothing(Class<T> exceptionClass)
            throws XmlPullParserException, IOException, ParseException, CancelException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Throwable e = (Throwable)createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        expect(gpxToCache.load()).andStubReturn(false);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        cachePersisterFacade.close(false);

        replay(e);
        replay(errorDisplayer);
        replay(cachePersisterFacade);
        replay(gpxToCache);
        assertFalse(new GpxLoader(gpxToCache, cachePersisterFacade, errorDisplayer).load());
        verify(cachePersisterFacade);
        verify(gpxToCache);
        verify(errorDisplayer);
    }

    public void testAbortLoad() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);

        gpxToCache.abort();

        replay(gpxToCache);
        new GpxLoader(gpxToCache, null, null).abort();
        verify(gpxToCache);
    }

    public void testEnd() {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.end();

        replay(cachePersisterFacade);
        new GpxLoader(null, cachePersisterFacade, null).end();
        verify(cachePersisterFacade);
    }

    public void testLoad() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);

        expect(gpxToCache.load()).andReturn(false);
        cachePersisterFacade.close(true);

        replay(cachePersisterFacade);
        replay(gpxToCache);
        assertTrue(new GpxLoader(gpxToCache, cachePersisterFacade, null).load());
        verify(cachePersisterFacade);
        verify(gpxToCache);
    }

    public void testLoadAlreadyLoaded() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);

        expect(gpxToCache.load()).andReturn(true);
        cachePersisterFacade.close(false);

        replay(cachePersisterFacade);
        replay(gpxToCache);
        assertTrue(new GpxLoader(gpxToCache, cachePersisterFacade, null).load());
        verify(cachePersisterFacade);
        verify(gpxToCache);
    }

    public void testLoadCancelException() throws XmlPullParserException, IOException,
            ParseException, CancelException {
        loadRaiseAndDisplayNothing(CancelException.class);
    }

    public void testLoadIOException() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        loadRaiseAndDisplayCustomMessage(IOException.class, R.string.error_reading_file);
    }

    public void testLoadPullParserException() throws XmlPullParserException, IOException,
            ParseException, CancelException {
        loadRaiseAndDisplayExceptionMessage(R.string.error_parsing_file,
                XmlPullParserException.class);
    }

    public void testLoadSqliteException() throws XmlPullParserException, IOException,
            ParseException, CancelException {
        loadRaiseAndDisplayExceptionMessage(R.string.error_writing_cache, SQLiteException.class);
    }

    public void testOpen() throws XmlPullParserException, IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Reader reader = createMock(Reader.class);

        gpxToCache.open("/sdcard/foo.gpx", reader);
        cachePersisterFacade.open("/sdcard/foo.gpx");

        replay(cachePersisterFacade);
        replay(gpxToCache);
        new GpxLoader(gpxToCache, cachePersisterFacade, null).open("/sdcard/foo.gpx", reader);
        verify(cachePersisterFacade);
        verify(gpxToCache);
    }

    public void testStart() {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.start();

        replay(cachePersisterFacade);
        new GpxLoader(null, cachePersisterFacade, null).start();
        verify(cachePersisterFacade);
    }

}
