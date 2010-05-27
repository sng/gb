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
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.EventHelperDI.EventHelperFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class
})
public class ImportThreadDelegateTest {

    private FileDataVersionWriter fileDataVersionWriter;
    private OldCacheFilesCleaner oldCacheFilesCleaner;
    private FileDataVersionChecker fileDataVersionChecker;
    private DbFrontend dbFrontend;
    private GpxLoader gpxLoader;
    private Provider<String> gpxNameProvider;
    private Provider<String> userNameProvider;
    private GpxAndZipFiles gpxAndZipFiles;
    private IGpxReader gpxFile;
    private Reader reader;
    private EventHelperFactory eventHelperFactory;
    private EventHelper eventHelper;
    private EventHandler eventHandler;
    private EventHandlers eventHandlers;
    private GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter;
    private ImportThreadHelper importThreadHelper;
    private IGpxReader iGpxReader;
    private MessageHandlerInterface messageHandler;
    private ErrorDisplayer errorDisplayer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        fileDataVersionWriter = PowerMock.createMock(FileDataVersionWriter.class);
        oldCacheFilesCleaner = PowerMock.createMock(OldCacheFilesCleaner.class);
        fileDataVersionChecker = PowerMock.createMock(FileDataVersionChecker.class);
        dbFrontend = PowerMock.createMock(DbFrontend.class);
        gpxLoader = PowerMock.createMock(GpxLoader.class);
        gpxNameProvider = PowerMock.createMock(Provider.class);
        userNameProvider = PowerMock.createMock(Provider.class);
        gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        gpxFile = PowerMock.createMock(IGpxReader.class);
        reader = PowerMock.createMock(Reader.class);
        eventHelperFactory = PowerMock.createMock(EventHelperFactory.class);
        eventHelper = PowerMock.createMock(EventHelper.class);
        eventHandler = PowerMock.createMock(EventHandler.class);
        eventHandlers = PowerMock.createMock(EventHandlers.class);
        gpxFilesAndZipFilesIter = PowerMock.createMock(GpxFilesAndZipFilesIter.class);
        importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        iGpxReader = PowerMock.createMock(IGpxReader.class);
        messageHandler = PowerMock.createMock(MessageHandler.class);
        errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
    }

    @Test
    public void testHelperCleanup() {
        messageHandler.loadComplete();

        PowerMock.replayAll();
        new ImportThreadHelper(null, messageHandler, null, null, null, null, null).cleanup();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperEndNoFiles() {
        EasyMock.expect(userNameProvider.get()).andReturn("");
        EasyMock.expect(gpxNameProvider.get()).andReturn("/sdcard/download");
        gpxLoader.end();

        PowerMock.replayAll();
        try {
            new ImportThreadHelper(gpxLoader, null, null, null, oldCacheFilesCleaner,
                    userNameProvider, gpxNameProvider).end();
            assertTrue("Expected ImportException, but didn't get one.", false);
        } catch (ImportException e) {
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFileAndEnd() throws XmlPullParserException, IOException,
            ImportException {
        EasyMock.expect(gpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(eventHandlers.get("gpx")).andReturn(eventHandler);
        EasyMock.expect(eventHelperFactory.create(eventHandler)).andReturn(eventHelper);
        EasyMock.expect(gpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        EasyMock.expect(gpxLoader.load(eventHelper)).andReturn(true);
        gpxLoader.end();

        PowerMock.replayAll();
        ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader, null,
                eventHelperFactory, eventHandlers, null, null, null);
        assertTrue(importThreadHelper.processFile(gpxFile));
        importThreadHelper.end();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperStart() {
        oldCacheFilesCleaner.clean();
        gpxLoader.start();

        PowerMock.replayAll();
        new ImportThreadHelper(gpxLoader, null, null, null, oldCacheFilesCleaner, null, null)
                .start();
        PowerMock.verifyAll();
    }

    @Test
    public void testRun() throws FileNotFoundException, XmlPullParserException, IOException,
            ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(true);
        dbFrontend.forceUpdate();
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxFilesAndZipFilesIter.next()).andReturn(iGpxReader);
        expect(importThreadHelper.processFile(iGpxReader)).andReturn(true);
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        importThreadHelper.end();
        importThreadHelper.cleanup();
        fileDataVersionWriter.writeVersion();
        importThreadHelper.startBCachingImport();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunAborted() throws FileNotFoundException, XmlPullParserException, IOException,
            ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxFilesAndZipFilesIter.next()).andReturn(iGpxReader);
        expect(importThreadHelper.processFile(iGpxReader)).andReturn(false);
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunFileNotFound() {
        final FileNotFoundException e = new FileNotFoundException("foo.gpx");

        errorDisplayer.displayError(R.string.error_opening_file, "foo.gpx");
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null) {
            @Override
            protected void tryRun() throws IOException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();

    }

    @Test
    public void testRunXmlPullParserException() {
        final XmlPullParserException e = PowerMock.createMock(XmlPullParserException.class);

        EasyMock.expect(e.getMessage()).andReturn("xml exception");
        errorDisplayer.displayError(R.string.error_parsing_file, "xml exception");
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null) {
            @Override
            protected void tryRun() throws XmlPullParserException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();

    }

    @Test
    public void testRunIOException() {
        final IOException e = PowerMock.createMock(IOException.class);
        EasyMock.expect(e.getMessage()).andReturn("problem reading file");
        importThreadHelper.cleanup();
        errorDisplayer.displayError(R.string.error_reading_file, "problem reading file");

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null) {
            @Override
            protected void tryRun() throws IOException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunIteratorFail() throws ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andThrow(
                new ImportException(R.string.error_cant_read_sd, "/sdcard-path"));
        errorDisplayer.displayError(R.string.error_cant_read_sd, "/sdcard-path");
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, fileDataVersionChecker,
                dbFrontend);
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunNoFiles() throws IOException, ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        importThreadHelper.start();
        fileDataVersionWriter.writeVersion();
        importThreadHelper.end();
        importThreadHelper.cleanup();
        importThreadHelper.startBCachingImport();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunNoFilesToImport() throws FileNotFoundException, IOException, ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        fileDataVersionWriter.writeVersion();
        importThreadHelper.end();
        importThreadHelper.cleanup();
        importThreadHelper.startBCachingImport();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunThrowRightAway() throws ImportException {
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andThrow(new RuntimeException());
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        try {
            new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null,
                    fileDataVersionWriter, fileDataVersionChecker, dbFrontend).run();
        } catch (Exception e) {

        }
        PowerMock.verifyAll();
    }
}
