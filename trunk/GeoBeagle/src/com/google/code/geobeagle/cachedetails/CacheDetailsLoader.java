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
        private final Activity activity;
        private final String path;
        private final int resourceId;

        DetailsError(Activity activity, int resourceId, String path) {
            this.activity = activity;
            this.resourceId = resourceId;
            this.path = path;
        }

        public String getString() {
            return activity.getString(resourceId, path);
        }
    }

    static class DetailsImpl implements Details {
        private final byte[] buffer;

        DetailsImpl(byte[] buffer) {
            this.buffer = buffer;
        }

        public String getString() {
            return new String(buffer);
        }
    }

    public static class DetailsOpener {
        private final Activity activity;
        private final FileDataVersionChecker fileDataVersionChecker;

        @Inject
        public DetailsOpener(Activity activity, FileDataVersionChecker fileDataVersionChecker) {
            this.activity = activity;
            this.fileDataVersionChecker = fileDataVersionChecker;
        }

        DetailsReader open(File file) {
            File sdcardPath = new File(CacheDetailsLoader.SDCARD_DIR);
            if (!sdcardPath.isDirectory())
                return new DetailsReaderError(activity, R.string.error_cant_read_sdroot, "");
            
            FileInputStream fileInputStream;
            String absolutePath = file.getAbsolutePath();
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                int error = fileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                        : R.string.error_opening_details_file;
                return new DetailsReaderError(activity, error, e.getMessage());
            }
            byte[] buffer = new byte[(int)file.length()];
            return new DetailsReaderImpl(activity, absolutePath, fileInputStream, buffer);
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
            this.mActivity = activity;
            this.mPath = path;
            this.mError = error;
        }

        public Details read() {
            return new DetailsError(mActivity, mError, mPath);
        }
    }

    static class DetailsReaderImpl implements DetailsReader {
        private final Activity activity;
        private final byte[] buffer;
        private final FileInputStream fileInputStream;
        private final String path;

        DetailsReaderImpl(Activity activity, String path, FileInputStream fileInputStream,
                byte[] buffer) {
            this.activity = activity;
            this.fileInputStream = fileInputStream;
            this.path = path;
            this.buffer = buffer;
        }

        public Details read() {
            try {
                fileInputStream.read(buffer);
                fileInputStream.close();
                return new DetailsImpl(buffer);
            } catch (IOException e) {
                return new DetailsError(activity, R.string.error_reading_details_file, path);
            }
        }
    }

    public static final String SDCARD_DIR = "/sdcard/";
    public static final String DETAILS_DIR = SDCARD_DIR + "GeoBeagle/data/";
    public static final String OLD_DETAILS_DIR = SDCARD_DIR + "GeoBeagle";
    private final DetailsOpener detailsOpener;
    private final FilePathStrategy filePathStrategy;

    @Inject
    public CacheDetailsLoader(DetailsOpener detailsOpener, FilePathStrategy filePathStrategy) {
        this.detailsOpener = detailsOpener;
        this.filePathStrategy = filePathStrategy;
    }

    public String load(CharSequence sourceName, CharSequence cacheId) {
        String path = filePathStrategy.getPath(sourceName, cacheId.toString());
        File file = new File(path);
        DetailsReader detailsReader = detailsOpener.open(file);
        Details details = detailsReader.read();
        return details.getString();
    }
}
