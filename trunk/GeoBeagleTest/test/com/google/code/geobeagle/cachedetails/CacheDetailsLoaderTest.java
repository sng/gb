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

package com.google.code.geobeagle.cachedetails;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsOpener;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReader;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderError;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderImpl;
import com.google.code.geobeagle.xmlimport.CacheTagsToDetails;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

@PrepareForTest({
        Activity.class, DetailsOpener.class, DetailsReaderImpl.class, Environment.class,
        File.class, CacheDetailsLoader.class, XmlPullParserException.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsLoaderTest {

    private FileDataVersionChecker fileDataVersionChecker;
    private EventHandlerGpx eventHandler;
    private CacheTagsToDetails cacheTagsToDetails;

    @Before
    public void setUp() {
        fileDataVersionChecker = createMock(FileDataVersionChecker.class);
        eventHandler = createMock(EventHandlerGpx.class);
        cacheTagsToDetails = createMock(CacheTagsToDetails.class);
    }

    @Test
    public void testDetailsOpener() throws Exception {
        File file = createMock(File.class);
        DetailsReaderImpl detailsReaderImpl = createMock(DetailsReaderImpl.class);
        Activity activity = createMock(Activity.class);
        FileReader fileReader = createMock(FileReader.class);
        EventHelper eventHelper = createMock(EventHelper.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        StringWriterWrapper stringWriterWrapper = createMock(StringWriterWrapper.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);

        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andReturn(fileReader);
        expectNew(BufferedReader.class, fileReader).andReturn(bufferedReader);
        expectNew(DetailsReaderImpl.class, eq(activity), eq(bufferedReader), eq("/sdcard/foo.gpx"),
                eq(eventHelper), eq(eventHandler), eq(xmlPullParser), eq(stringWriterWrapper))
                .andReturn(detailsReaderImpl);

        replayAll();
        assertEquals(detailsReaderImpl, new DetailsOpener(activity, fileDataVersionChecker,
                eventHelper, eventHandler, xmlPullParser, stringWriterWrapper).open(file));
        verifyAll();
    }

    @Test
    public void testDetailsOpenerFileNotFound() throws Exception {
        File file = createMock(File.class);
        DetailsReaderError detailsReaderError = createMock(DetailsReaderError.class);
        Activity activity = createMock(Activity.class);

        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andThrow(
                new FileNotFoundException("/sdcard/foo.gpx"));
        expectNew(DetailsReaderError.class, activity, R.string.error_opening_details_file,
                "/sdcard/foo.gpx").andReturn(detailsReaderError);

        replayAll();
        assertEquals(detailsReaderError, new DetailsOpener(activity, fileDataVersionChecker, null,
                null, null, null).open(file));
        verifyAll();
    }

    @Test
    public void testDetailsReader() throws Exception {
        Activity activity = createMock(Activity.class);
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        Reader reader = createMock(Reader.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        eventHelper.open("/sdcard/foo.gpx", eventHandler);
        StringWriterWrapper stringWriterWrapper = new StringWriterWrapper();
        stringWriterWrapper.write("DETAILS");
        xmlPullParserWrapper.open("/sdcard/foo.gpx", reader);
        expect(xmlPullParserWrapper.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(
                eventHelper.handleEvent(XmlPullParser.END_DOCUMENT, eventHandler,
                        cacheTagsToDetails)).andReturn(true);

        replayAll();
        assertEquals("DETAILS",
                new DetailsReaderImpl(activity, reader, "/sdcard/foo.gpx", eventHelper,
                        eventHandler, xmlPullParserWrapper, stringWriterWrapper)
                        .read(cacheTagsToDetails));
        verifyAll();
    }

    @Test
    public void testDetailsReaderFileNotFound() throws Exception {
        Activity activity = createMock(Activity.class);
        expect(activity.getString(R.string.error_opening_details_file, "/sdcard/foo.html"))
                .andReturn("Can't open file /sdcard/foo.html");

        replayAll();
        assertEquals("Can't open file /sdcard/foo.html", new DetailsReaderError(activity,
                R.string.error_opening_details_file, "/sdcard/foo.html").read(cacheTagsToDetails));
        verifyAll();
    }

    @Test
    public void testDetailsReaderXmlPullParserException() throws Exception {
        Activity activity = createMock(Activity.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        Reader reader = createMock(Reader.class);
        XmlPullParserException xmlPullParserException = createMock(XmlPullParserException.class);
        StringWriterWrapper stringWriterWrapper = createMock(StringWriterWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(activity.getString(R.string.error_reading_details_file, "/sdcard/foo.gpx"))
                .andReturn("Can't open file /sdcard/foo.gpx");
        eventHelper.open("/sdcard/foo.gpx", eventHandler);
        xmlPullParser.open("/sdcard/foo.gpx", reader);
        expectLastCall().andThrow(xmlPullParserException);
        expect(xmlPullParserException.fillInStackTrace()).andStubReturn(xmlPullParserException);

        replayAll();
        assertEquals("Can't open file /sdcard/foo.gpx",
                new DetailsReaderImpl(activity, reader, "/sdcard/foo.gpx", eventHelper,
                        eventHandler, xmlPullParser, stringWriterWrapper).read(cacheTagsToDetails));
        verifyAll();
    }

    @Test
    public void testLoad() throws Exception {
        DetailsOpener detailsOpener = createMock(DetailsOpener.class);
        File file = createMock(File.class);
        DetailsReader detailsReader = createMock(DetailsReader.class);
        FilePathStrategy filePathStrategy = createMock(FilePathStrategy.class);

        expect(filePathStrategy.getPath("foo.gpx", "GC123", "gpx")).andReturn(
                "/sdcard/details/foo.gpx/GC123.html");
        expectNew(File.class, "/sdcard/details/foo.gpx/GC123.html").andReturn(file);
        expect(detailsOpener.open(file)).andReturn(detailsReader);
        expect(detailsReader.read(cacheTagsToDetails)).andReturn("cache details");

        replayAll();
        assertEquals("cache details", new CacheDetailsLoader(detailsOpener, filePathStrategy,
                cacheTagsToDetails).load("foo.gpx", "GC123"));
        verifyAll();
    }
}
