
package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        GpxFileOpener.class, ZipFileIter.class, ZipFileOpener.class
})
public class GpxImporterTest {

    @Test
    public void testAbort() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(false);
        messageHandler.abortLoad();

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoader, null, importThreadWrapper, messageHandler, null, null,
                null).abort();
        PowerMock.verifyAll();
    }

    @Test
    public void testAbortThreadAlive() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);
        ToastFactory toastFactory = PowerMock.createMock(ToastFactory.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(true);
        messageHandler.abortLoad();
        importThreadWrapper.join();
        toastFactory.showToast(listActivity, R.string.import_canceled, Toast.LENGTH_SHORT);

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoader, listActivity, importThreadWrapper, messageHandler,
                toastFactory, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    public void testImportGpxs() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        EventHandlers eventHandlers = PowerMock.createMock(EventHandlers.class);
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        geocacheListPresenter.onPause();
        importThreadWrapper.open(cacheListRefresh, gpxLoader, eventHandlers, null);
        importThreadWrapper.start();

        PowerMock.replayAll();
        new GpxImporter(geocacheListPresenter, gpxLoader, null, importThreadWrapper, null, null,
                eventHandlers, null).importGpxs(cacheListRefresh);
        PowerMock.verifyAll();
    }
}
