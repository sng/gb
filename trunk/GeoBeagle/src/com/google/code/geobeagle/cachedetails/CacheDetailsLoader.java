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

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CacheDetailsLoader {

    static interface Details {
        String getString();
    }

    static class DetailsError implements Details {
        private final Activity mActivity;
        private final String mPath;
        private final int mResourceId;

        DetailsError(Activity activity, int resourceId, String path) {
            mActivity = activity;
            mResourceId = resourceId;
            mPath = path;
        }

        public String getString() {
            return mActivity.getString(mResourceId, mPath);
        }
    }

    static class DetailsImpl implements Details {
        private final byte[] mBuffer;

        DetailsImpl(byte[] buffer) {
            mBuffer = buffer;
        }

        public String getString() {
            return new String(mBuffer);
        }
    }

    public static class DetailsOpener {
        private final Activity mActivity;
        private final FileDataVersionChecker mFileDataVersionChecker;

        @Inject
        public DetailsOpener(Activity activity, FileDataVersionChecker fileDataVersionChecker) {
            mActivity = activity;
            mFileDataVersionChecker = fileDataVersionChecker;
        }

        DetailsReader open(File file) {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return new DetailsReaderError(mActivity, R.string.error_cant_read_sdroot, state);
            }
            FileInputStream fileInputStream;
            String absolutePath = file.getAbsolutePath();
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                int error = mFileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                        : R.string.error_opening_details_file;
                return new DetailsReaderError(mActivity, error, e.getMessage());
            }
            byte[] buffer = new byte[(int)file.length()];
            return new DetailsReaderImpl(mActivity, absolutePath, fileInputStream, buffer);
        }
    }

    interface DetailsReader {
        Details read();
    }

    static class DetailsReaderError implements DetailsReader {
        private final Activity mActivity;
        private final String mPath;
        private final int mError;

        DetailsReaderError(Activity activity, int error, String path) {
            mActivity = activity;
            mPath = path;
            mError = error;
        }

        public Details read() {
            return new DetailsError(mActivity, mError, mPath);
        }
    }

    static class DetailsReaderImpl implements DetailsReader {
        private final Activity mActivity;
        private final byte[] mBuffer;
        private final FileInputStream mFileInputStream;
        private final String mPath;

        DetailsReaderImpl(Activity activity, String path, FileInputStream fileInputStream,
                byte[] buffer) {
            mActivity = activity;
            mFileInputStream = fileInputStream;
            mPath = path;
            mBuffer = buffer;
        }

        public Details read() {
            try {
                mFileInputStream.read(mBuffer);
                mFileInputStream.close();
                return new DetailsImpl(mBuffer);
            } catch (IOException e) {
                return new DetailsError(mActivity, R.string.error_reading_details_file, mPath);
            }
        }
    }

    private final DetailsOpener mDetailsOpener;
    private final FilePathStrategy mFilePathStrategy;

    @Inject
    public CacheDetailsLoader(DetailsOpener detailsOpener, FilePathStrategy filePathStrategy) {
        mDetailsOpener = detailsOpener;
        mFilePathStrategy = filePathStrategy;
    }

    public String load(CharSequence sourceName, CharSequence cacheId) {
        String path = mFilePathStrategy.getPath(sourceName, cacheId.toString(), "html");
        File file = new File(path);
        DetailsReader detailsReader = mDetailsOpener.open(file);
        Details details = detailsReader.read();
        return details.getString();
    }
}
