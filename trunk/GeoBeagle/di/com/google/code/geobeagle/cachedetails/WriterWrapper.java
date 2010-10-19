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

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriterWrapper implements com.google.code.geobeagle.cachedetails.Writer {

    private java.io.Writer mWriter;

    @Override
    public void close() throws IOException {
        mWriter.close();
        mWriter = null;
    }

    @Override
    public void open(String path, String cacheId) throws IOException {
        mWriter = new BufferedWriter(new FileWriter(path), 4000);
    }

    @Override
    public void mkdirs(String path) {
        new File(new File(path).getParent()).mkdirs();
    }

    @Override
    public void write(String str) throws IOException {
        if (mWriter == null) {
            Log.e("GeoBeagle", "Attempting to write string but no waypoint received yet: " + str);
            return;
        }
        try {
            mWriter.write(str);
        } catch (IOException e) {
            throw new IOException("Error writing line '" + str + "'");
        }
    }

    @Override
    public boolean isOpen() {
        return mWriter != null;
    }
}
