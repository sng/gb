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

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.HtmlWriter;
import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.xmlimport.FileFactory;
import com.google.code.geobeagle.xmlimport.CachePersisterFacade.TextHandler;
import com.google.code.geobeagle.xmlimport.EventHelperDI.EventHelperFactory;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.code.geobeagle.xmlimport.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.FilenameFilter;
import java.util.HashMap;

public class GpxImporterDI {
    // Can't test this due to final methods in base.
    public static class ImportThread extends Thread {
        static ImportThread create(MessageHandler messageHandler, GpxLoader gpxLoader,
                EventHandlers eventHandlers, XmlPullParserWrapper xmlPullParserWrapper,
                ErrorDisplayer errorDisplayer, Aborter aborter, 
                GeocacheFactory geocacheFactory) {
            final GpxFilenameFilter gpxFilenameFilter = new GpxFilenameFilter();
            final FilenameFilter filenameFilter = new GpxAndZipFilenameFilter(gpxFilenameFilter);
            final ZipInputFileTester zipInputFileTester = new ZipInputFileTester(gpxFilenameFilter);
            final GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory = new GpxFileIterAndZipFileIterFactory(
                    zipInputFileTester, aborter);
            final GpxAndZipFiles gpxAndZipFiles = new GpxAndZipFiles(filenameFilter,
                    gpxFileIterAndZipFileIterFactory);
            final EventHelperFactory eventHelperFactory = new EventHelperFactory(
                    xmlPullParserWrapper);
            final ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader,
                    messageHandler, eventHelperFactory, eventHandlers, errorDisplayer,
                    geocacheFactory);
            return new ImportThread(gpxAndZipFiles, importThreadHelper, errorDisplayer);
        }

        private final ImportThreadDelegate mImportThreadDelegate;

        public ImportThread(GpxAndZipFiles gpxAndZipFiles, ImportThreadHelper importThreadHelper,
                ErrorDisplayer errorDisplayer) {
            mImportThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper,
                    errorDisplayer);
        }

        @Override
        public void run() {
            mImportThreadDelegate.run();
        }
    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ImportThreadWrapper {
        private final Aborter mAborter;
        private ImportThread mImportThread;
        private final MessageHandler mMessageHandler;
        private final XmlPullParserWrapper mXmlPullParserWrapper;

        public ImportThreadWrapper(MessageHandler messageHandler,
                XmlPullParserWrapper xmlPullParserWrapper, Aborter aborter) {
            mMessageHandler = messageHandler;
            mXmlPullParserWrapper = xmlPullParserWrapper;
            mAborter = aborter;
        }

        public boolean isAlive() {
            if (mImportThread != null)
                return mImportThread.isAlive();
            return false;
        }

        public void join() {
            if (mImportThread != null)
                try {
                    mImportThread.join();
                } catch (InterruptedException e) {
                    // Ignore; we are aborting anyway.
                }
        }

        public void open(CacheListAdapter cacheList, GpxLoader gpxLoader,
                EventHandlers eventHandlers, ErrorDisplayer mErrorDisplayer,
                GeocacheFactory geocacheFactory) {
            mMessageHandler.start(cacheList);
            mImportThread = ImportThread.create(mMessageHandler, gpxLoader, eventHandlers,
                    mXmlPullParserWrapper, mErrorDisplayer, mAborter, geocacheFactory);
        }

        public void start() {
            if (mImportThread != null)
                mImportThread.start();
        }
    }

    // Too hard to test this class due to final methods in base.
    public static class MessageHandler extends Handler {
        public static final String GEOBEAGLE = "GeoBeagle";
        static final int MSG_DONE = 1;
        static final int MSG_PROGRESS = 0;

        public static MessageHandler create(ListActivity listActivity) {
            final ProgressDialogWrapper progressDialogWrapper = new ProgressDialogWrapper(
                    listActivity);
            return new MessageHandler(progressDialogWrapper);
        }

        private int mCacheCount;
        private boolean mLoadAborted;
        private CacheListAdapter mCacheList;
        private final ProgressDialogWrapper mProgressDialogWrapper;
        private String mSource;
        private String mStatus;
        private String mWaypointId;

        public MessageHandler(ProgressDialogWrapper progressDialogWrapper) {
            mProgressDialogWrapper = progressDialogWrapper;
        }

        public void abortLoad() {
            mLoadAborted = true;
            mProgressDialogWrapper.dismiss();
        }

        @Override
        public void handleMessage(Message msg) {
            // Log.d(GEOBEAGLE, "received msg: " + msg.what);
            switch (msg.what) {
                case MessageHandler.MSG_PROGRESS:
                    mProgressDialogWrapper.setMessage(mStatus);
                    break;
                case MessageHandler.MSG_DONE:
                    if (!mLoadAborted) {
                        mProgressDialogWrapper.dismiss();
                        mCacheList.forceRefresh();
                    }
                    break;
                default:
                    break;
            }
        }

        public void loadComplete() {
            sendEmptyMessage(MessageHandler.MSG_DONE);
        }

        public void start(CacheListAdapter cacheList) {
            mCacheCount = 0;
            mLoadAborted = false;
            mCacheList = cacheList;
            // TODO: move text into resource.
            mProgressDialogWrapper.show("Syncing caches", "Please wait...");
        }

        public void updateName(String name) {
            mStatus = mCacheCount++ + ": " + mSource + " - " + mWaypointId + " - " + name;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void updateSource(String text) {
            mSource = text;
            mStatus = "Opening: " + mSource + "...";
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void updateWaypointId(String wpt) {
            mWaypointId = wpt;
        }
    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ProgressDialogWrapper {
        private final Context mContext;
        private ProgressDialog mProgressDialog;

        public ProgressDialogWrapper(Context context) {
            mContext = context;
        }

        public void dismiss() {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        public void setMessage(CharSequence message) {
            mProgressDialog.setMessage(message);
        }

        public void show(String title, String msg) {
            mProgressDialog = ProgressDialog.show(mContext, title, msg);
        }
    }

    public static class ToastFactory {
        public void showToast(Context context, int resId, int duration) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    public static GpxImporter create(ListActivity listActivity,
            XmlPullParserWrapper xmlPullParserWrapper, ErrorDisplayer errorDisplayer,
            GeoFixProvider geoFixProvider, Aborter aborter,
            MessageHandler messageHandler,
            CacheWriter cacheWriter, CacheTypeFactory cacheTypeFactory,
            GeocacheFactory geocacheFactory) {
        final PowerManager powerManager = (PowerManager)listActivity
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");
        FileFactory fileFactory = new FileFactory();
        WriterWrapper writerWrapper = new WriterWrapper();
        HtmlWriter htmlWriter = new HtmlWriter(writerWrapper);
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter);
        final CacheTagSqlWriter cacheTagSqlWriter = new CacheTagSqlWriter(cacheWriter, cacheTypeFactory);

        final SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(listActivity);
        final String username = sharedPreferences.getString("username", "");
        final CachePersisterFacade cachePersisterFacade = 
            new CachePersisterFacade(cacheTagSqlWriter, fileFactory, 
                    cacheDetailsWriter, messageHandler, wakeLock, username);

        final GpxLoader gpxLoader = GpxLoaderDI.create(cachePersisterFacade, xmlPullParserWrapper,
                aborter, errorDisplayer, wakeLock);
        final ToastFactory toastFactory = new ToastFactory();
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler,
                xmlPullParserWrapper, aborter);
        final HashMap<String, TextHandler> textHandlers = GpxImporterDI.initializeTextHandlers(cachePersisterFacade);
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade, textHandlers );
        final EventHandlerLoc eventHandlerLoc = new EventHandlerLoc(cachePersisterFacade);

        final EventHandlers eventHandlers = new EventHandlers();
        eventHandlers.add(".gpx", eventHandlerGpx);
        eventHandlers.add(".loc", eventHandlerLoc);

        return new GpxImporter(geoFixProvider, gpxLoader, listActivity, importThreadWrapper,
                messageHandler, toastFactory, eventHandlers, errorDisplayer,
                geocacheFactory);
    }

    public static HashMap<String, TextHandler> initializeTextHandlers(CachePersisterFacade cachePersisterFacade) {
        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>(); 
        textHandlers.put(EventHandlerGpx.XPATH_WPTNAME, cachePersisterFacade.wptName);
        textHandlers.put(EventHandlerGpx.XPATH_WPTDESC, cachePersisterFacade.wptDesc);
        textHandlers.put(EventHandlerGpx.XPATH_GROUNDSPEAKNAME, cachePersisterFacade.groundspeakName);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHENAME, cachePersisterFacade.groundspeakName);
        textHandlers.put(EventHandlerGpx.XPATH_PLACEDBY, cachePersisterFacade.placedBy);
        textHandlers.put(EventHandlerGpx.XPATH_LOGDATE, cachePersisterFacade.logDate);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHELOGDATE, cachePersisterFacade.logDate);
        
        textHandlers.put(EventHandlerGpx.XPATH_HINT, cachePersisterFacade.hint);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHEHINT, cachePersisterFacade.hint);
        
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_TYPE, cachePersisterFacade.cacheType);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHE_TYPE, cachePersisterFacade.cacheType);
        textHandlers.put(EventHandlerGpx.XPATH_WAYPOINT_TYPE, cachePersisterFacade.cacheType);
        
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_DIFFICULTY, cachePersisterFacade.difficulty);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHE_DIFFICULTY, cachePersisterFacade.difficulty);
    
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_TERRAIN, cachePersisterFacade.terrain);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHE_TERRAIN, cachePersisterFacade.terrain);
        
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_CONTAINER, cachePersisterFacade.container);
        textHandlers.put(EventHandlerGpx.XPATH_GEOCACHE_CONTAINER, cachePersisterFacade.container);
        return textHandlers;
    }

}
