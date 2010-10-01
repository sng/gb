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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.xmlimport.GpxLoader.GpxLoaderFromFile;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.content.Context;
import android.widget.Toast;

public class GpxImporter implements Abortable {
    private final ErrorDisplayer mErrorDisplayer;
    private final EventHandler mEventHandler;
    private final GpxLoader mGpxLoader;
    private final ImportThreadWrapper mImportThreadWrapper;
    private final Provider<Context> mContextProvider;
    private final MessageHandlerInterface mMessageHandler;
    private final ToastFactory mToastFactory;
    private final Pausable mGeocacheListPresenter;
    private final Provider<CacheListRefresh> mCacheListRefreshProvider;
    private final Injector mInjector;

    @Inject
    public GpxImporter(GeocacheListPresenter geocacheListPresenter,
            GpxLoaderFromFile gpxLoader,
            Provider<Context> contextProvider,
            ImportThreadWrapper importThreadWrapper,
            MessageHandler messageHandler,
            ToastFactory toastFactory,
            EventHandlerComposite eventHandler,
            ErrorDisplayer errorDisplayer,
            Provider<CacheListRefresh> cacheListRefreshProvider,
            Injector injector) {
        mContextProvider = contextProvider;
        mGpxLoader = gpxLoader;
        mEventHandler = eventHandler;
        mImportThreadWrapper = importThreadWrapper;
        mMessageHandler = messageHandler;
        mErrorDisplayer = errorDisplayer;
        mToastFactory = toastFactory;
        mGeocacheListPresenter = geocacheListPresenter;
        mCacheListRefreshProvider = cacheListRefreshProvider;
        mInjector = injector;
    }

    @Override
    public void abort() {
        mMessageHandler.abortLoad();
        mGpxLoader.abort();
        if (mImportThreadWrapper.isAlive()) {
            mImportThreadWrapper.join();
            mToastFactory.showToast(mContextProvider.get(), R.string.import_canceled,
                    Toast.LENGTH_SHORT);
        }
    }

    public void importGpxs() {
        mGeocacheListPresenter.onPause();

        mImportThreadWrapper.open(mCacheListRefreshProvider.get(), mGpxLoader, mEventHandler,
                mErrorDisplayer,
                mInjector);
        mImportThreadWrapper.start();
    }
}
