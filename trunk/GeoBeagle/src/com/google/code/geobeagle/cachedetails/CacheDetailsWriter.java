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

import com.google.code.geobeagle.activity.main.Util;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;

public class CacheDetailsWriter {
    private final HtmlWriter mHtmlWriter;
    private final FilePathStrategy mFilePathStrategy;
    private String mLatitude;
    private String mLongitude;
    private String mGpxName;

    @Inject
    public CacheDetailsWriter(HtmlWriter htmlWriter, FilePathStrategy filePathStrategy) {
        mHtmlWriter = htmlWriter;
        mFilePathStrategy = filePathStrategy;
    }

    public void close() throws IOException {
        mHtmlWriter.writeFooter();
        mHtmlWriter.close();
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void open(String wpt) throws IOException {
        String path = mFilePathStrategy.getPath(mGpxName, wpt);

        new File(new File(path).getParent()).mkdirs();
        mHtmlWriter.open(path);
    }

    public static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public void writeHint(String text) throws IOException {
        mHtmlWriter.write("<br />Hint: <font color=gray>" + text + "</font>");
    }

    public void writeLine(String text) throws IOException {
        mHtmlWriter.write(text);
    }

    public void writeLogDate(String text) throws IOException {
        mHtmlWriter.writeSeparator();
        mHtmlWriter.write(text);
    }

    public void writeWptName(String wpt) throws IOException {
        mHtmlWriter.writeHeader();
        mHtmlWriter.write(wpt);
        mHtmlWriter.write(Util.formatDegreesAsDecimalDegreesString(Double.valueOf(mLatitude))
                + ", " + Util.formatDegreesAsDecimalDegreesString(Double.valueOf(mLongitude)));
        mLatitude = mLongitude = null;
    }

    public void gpxName(String gpxName) {
        mGpxName = gpxName;
    }
}
