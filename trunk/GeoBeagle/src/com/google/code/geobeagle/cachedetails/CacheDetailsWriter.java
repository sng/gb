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

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CacheDetailsWriter {
    private final HtmlWriter mHtmlWriter;
    private String mLatitude;
    private String mLongitude;
    private int mLogNumber;
    private final Emotifier mEmotifier;
    private final Context mContext;
    private String mTime;

    public CacheDetailsWriter(HtmlWriter htmlWriter, Emotifier emotifier, Context context) {
        mHtmlWriter = htmlWriter;
        mEmotifier = emotifier;
        mContext = context;
    }

    public void close() throws IOException {
        mHtmlWriter.writeFooter();
        mHtmlWriter.close();
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = (String)Util.formatDegreesAsDecimalDegreesString(Double.valueOf(latitude));
        mLongitude = (String)Util.formatDegreesAsDecimalDegreesString(Double.valueOf(longitude));
    }

    public static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public void writeHint(String text) throws IOException {
        mHtmlWriter
                .write("<a class=hint id=hint_link onclick=\"dht('hint_link');return false;\" href=#>"
                        + "Encrypt</a>");
        mHtmlWriter.write("<div id=hint_link_text>" + text + "</div>");
    }

    public void writeLine(String text) throws IOException {
        mHtmlWriter.writeln(text);
    }

    public static Date parse(String input) throws java.text.ParseException {
        // NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        // things a bit. Before we go on we have to repair this.
        String s = new String(input);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // this is zero time so we need to add that TZ indicator for
        if (input.endsWith("Z")) {
            s = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            s = s0 + "GMT" + s1;
        }
        Log.d("GeoBeagle", "PARSING: " + s);
        final Date parsed = df.parse(input);
        Log.d("GeoBeagle", "PARSED: " + parsed);
        return parsed;
    }

    public void writeLogDate(String text) throws IOException {
        mHtmlWriter.writeSeparator();
        try {
            Log.d("GeoBeagle", "writeLogDate: " + text);
            mHtmlWriter.writeln(getRelativeTime(text));
        } catch (ParseException e) {
            mHtmlWriter.writeln("error parsing date: " + e.getLocalizedMessage());
        }
    }

    private String getRelativeTime(String text) throws ParseException {
        Date date = parse(text);
        final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(mContext,
                date.getTime(), DateUtils.HOUR_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        String s = relativeDateTimeString.toString();
        Log.d("GeoBeagle", "grt: " + s);
        return s;
    }

    public void writeWptName() throws IOException {
        mHtmlWriter.open(null);
        writeField("Location", mLatitude + ", " + mLongitude);
        mLatitude = mLongitude = null;
    }

    public void writeLogText(String text, boolean encoded) throws IOException {
        String f;
        if (encoded)
            f = "<a class=hint id=log_%1$s onclick=\"dht('log_%1$s');return false;\" "
                    + "href=#>Encrypt</a><div id=log_%1$s_text>%2$s</div>";
        else
            f = "%2$s";

        mHtmlWriter.writeln(String.format(f, mLogNumber++, mEmotifier.emotify(text)));
    }

    public void logType(String trimmedText) throws IOException {
        mHtmlWriter.writeln(Emotifier.ICON_PREFIX + "log_" + trimmedText.replace(' ', '_')
                + Emotifier.ICON_SUFFIX + " " + trimmedText);
    }

    public void writeName(String name) throws IOException {
        mHtmlWriter.writeHeader();
        mHtmlWriter.write("<center><h3>" + name + "</h3></center>\n");
    }

    public void placedBy(String text) throws IOException {
        Log.d("GeoBeagle", "PLACED BY: " + mTime);
        String on = "";
        try {
            on = getRelativeTime(mTime);
        } catch (ParseException e) {
            on = "PARSE ERROR";
        }
        writeField("Placed by", text);
        writeField("Placed on", on);
    }

    public void writeField(String fieldName, String field) throws IOException {
        mHtmlWriter.writeln("<font color=grey>" + fieldName + "</font>: " + field);
    }

    public void wptTime(String time) {
        mTime = time;
    }

    public void writeShortDescription(String trimmedText) throws IOException {
        mHtmlWriter.writeSeparator();
        mHtmlWriter.writeln(trimmedText);
        mHtmlWriter.writeln("");
    }

    public void writeLongDescription(String trimmedText) throws IOException {
        mHtmlWriter.write(trimmedText);
        mHtmlWriter.writeSeparator();
    }
}
