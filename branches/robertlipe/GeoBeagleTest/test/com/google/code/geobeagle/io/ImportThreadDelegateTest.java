
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIter;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.easymock.EasyMock;
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

    @Test
    public void testHelperCleanup() {
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);

        messageHandler.loadComplete();

        PowerMock.replayAll();
        new ImportThreadHelper(null, messageHandler, null).cleanup();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperEnd() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);

        gpxLoader.end();

        PowerMock.replayAll();
        new ImportThreadHelper(gpxLoader, null, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFile() throws XmlPullParserException, IOException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        Reader reader = PowerMock.createMock(Reader.class);

        EasyMock.expect(iGpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(iGpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        EasyMock.expect(gpxLoader.load()).andReturn(true);

        PowerMock.replayAll();
        assertEquals(true, new ImportThreadHelper(gpxLoader, null, null).processFile(iGpxFile));
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFileLoadAborted() throws XmlPullParserException, IOException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        Reader reader = PowerMock.createMock(Reader.class);

        EasyMock.expect(iGpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(iGpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        EasyMock.expect(gpxLoader.load()).andReturn(false);

        PowerMock.replayAll();
        assertEquals(false, new ImportThreadHelper(gpxLoader, null, null).processFile(iGpxFile));
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFileLoadFileNotFound() throws XmlPullParserException, IOException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        Reader reader = PowerMock.createMock(Reader.class);
        Throwable e = (Throwable)PowerMock.createMock(FileNotFoundException.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        EasyMock.expect(iGpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(iGpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayError(R.string.error_opening_file, "foo.gpx");

        PowerMock.replayAll();
        assertEquals(false, new ImportThreadHelper(gpxLoader, null, errorDisplayer)
                .processFile(iGpxFile));
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFileLoadRandomException() throws XmlPullParserException,
            IOException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        Reader reader = PowerMock.createMock(Reader.class);
        Exception e = PowerMock.createMock(RuntimeException.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        EasyMock.expect(iGpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(iGpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayErrorAndStack(e);

        PowerMock.replayAll();
        assertEquals(false, new ImportThreadHelper(gpxLoader, null, errorDisplayer)
                .processFile(iGpxFile));
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperStart() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);

        gpxLoader.start();

        PowerMock.replayAll();
        assertEquals(true, new ImportThreadHelper(gpxLoader, null, null).start(true));
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperStartNoFiles() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        errorDisplayer.displayError(R.string.error_no_gpx_files);

        PowerMock.replayAll();
        assertEquals(false, new ImportThreadHelper(gpxLoader, null, errorDisplayer).start(false));
        PowerMock.verifyAll();
    }

    @Test
    public void testRun() throws FileNotFoundException, XmlPullParserException, IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxAndZipFilesIter gpxAndZipFilesIter = PowerMock.createMock(GpxAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);

        expect(gpxAndZipFiles.iterator()).andReturn(gpxAndZipFilesIter);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(true);
        expect(importThreadHelper.start(true)).andReturn(true);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxAndZipFilesIter.next()).andReturn(iGpxFile);
        expect(importThreadHelper.processFile(iGpxFile)).andReturn(true);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(false);
        importThreadHelper.end();
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunThrowRightAway() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);

        expect(gpxAndZipFiles.iterator()).andThrow(new RuntimeException());
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        try {
            new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null).run();
        } catch (Exception e) {

        }
        PowerMock.verifyAll();
    }

    @Test
    public void testRunIOException() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        final IOException e = PowerMock.createMock(IOException.class);

        errorDisplayer.displayErrorAndStack(e);
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer) {
            protected void tryRun() throws IOException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunIteratorFail() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        expect(gpxAndZipFiles.iterator()).andReturn(null);
        errorDisplayer.displayError(R.string.error_cant_read_sd);
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer);
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunAborted() throws FileNotFoundException, XmlPullParserException, IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxAndZipFilesIter gpxAndZipFilesIter = PowerMock.createMock(GpxAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);

        expect(gpxAndZipFiles.iterator()).andReturn(gpxAndZipFilesIter);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(true);
        expect(importThreadHelper.start(true)).andReturn(true);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxAndZipFilesIter.next()).andReturn(iGpxFile);
        expect(importThreadHelper.processFile(iGpxFile)).andReturn(false);
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunNoFilesToImport() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxAndZipFilesIter gpxAndZipFilesIter = PowerMock.createMock(GpxAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);

        expect(gpxAndZipFiles.iterator()).andReturn(gpxAndZipFilesIter);
        expect(gpxAndZipFilesIter.hasNext()).andReturn(false);
        expect(importThreadHelper.start(false)).andReturn(false);
        importThreadHelper.cleanup();

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null).run();
        PowerMock.verifyAll();
    }
}
