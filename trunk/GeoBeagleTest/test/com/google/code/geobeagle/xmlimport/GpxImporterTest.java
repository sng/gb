
package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.xmlimport.GpxLoader.GpxLoaderFromFile;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
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
        GpxLoaderFromFile gpxLoaderFromFile = PowerMock.createMock(GpxLoaderFromFile.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);

        gpxLoaderFromFile.abort();
        expect(importThreadWrapper.isAlive()).andReturn(false);
        messageHandler.abortLoad();

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoaderFromFile, null, importThreadWrapper, messageHandler, null, null,
                null, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAbortThreadAlive() {
        GpxLoaderFromFile gpxLoaderFromFile = PowerMock.createMock(GpxLoaderFromFile.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);
        ToastFactory toastFactory = PowerMock.createMock(ToastFactory.class);
        Provider<Context> contextProvider = PowerMock.createMock(Provider.class);
        Context context = PowerMock.createMock(Context.class);

        gpxLoaderFromFile.abort();
        expect(importThreadWrapper.isAlive()).andReturn(true);
        messageHandler.abortLoad();
        importThreadWrapper.join();
        expect(contextProvider.get()).andReturn(context);
        toastFactory.showToast(context, R.string.import_canceled, Toast.LENGTH_SHORT);

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoaderFromFile, contextProvider, importThreadWrapper, messageHandler,
                toastFactory, null, null, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImportGpxs() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        Provider<CacheListRefresh> cacheListRefreshProvider = PowerMock.createMock(Provider.class);
        GpxLoaderFromFile gpxLoader = PowerMock.createMock(GpxLoaderFromFile.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        expect(cacheListRefreshProvider.get()).andReturn(cacheListRefresh);
        geocacheListPresenter.onPause();
        importThreadWrapper.open(cacheListRefresh, gpxLoader, null, null, null);
        importThreadWrapper.start();

        PowerMock.replayAll();
        new GpxImporter(geocacheListPresenter, gpxLoader, null, importThreadWrapper, null, null,
                null, null, cacheListRefreshProvider, null).importGpxs();
        PowerMock.verifyAll();
    }
}
