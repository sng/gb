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

import java.io.IOException;

public class CacheDetailsWriter {
    public static final String GEOBEAGLE_DIR = "/sdcard/GeoBeagle";
    private final HtmlWriter mHtmlWriter;
    private String mLatitude;
    private String mLongitude;

    public CacheDetailsWriter(HtmlWriter htmlWriter) {
        mHtmlWriter = htmlWriter;
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
        final String sanitized = replaceIllegalFileChars(wpt);
        mHtmlWriter.open(CacheDetailsWriter.GEOBEAGLE_DIR + "/" + sanitized + ".html");
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
        mHtmlWriter.write(mLatitude + ", " + mLongitude);
        mLatitude = mLongitude = null;
    }
}
