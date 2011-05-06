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

package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.DetailsDatabaseReader;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class CacheLoader {
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsXmlToString detailsXmlToString;
    private final CacheReaderFromFile cacheReaderFromFile;
    private final Resources resources;

    CacheLoader(CacheReaderFromFile cacheReaderFromFile,
            DetailsDatabaseReader detailsDatabaseReader,
            DetailsXmlToString detailsXmlToString,
            Resources resources) {
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsXmlToString = detailsXmlToString;
        this.cacheReaderFromFile = cacheReaderFromFile;
        this.resources = resources;
    }

    public String load(CharSequence sourceName, CharSequence cacheId) throws CacheLoaderException {
        Reader reader = createReader(sourceName, cacheId);
        try {
            return detailsXmlToString.read(reader);
        } catch (XmlPullParserException e) {
            return resources.getString(R.string.error_reading_details_file, cacheId);
        } catch (IOException e) {
            return resources.getString(R.string.error_reading_details_file, cacheId);
        }
    }

    private Reader createReader(CharSequence sourceName, CharSequence cacheId)
            throws CacheLoaderException {
        String detailsFromDatabase = detailsDatabaseReader.read(cacheId);
        if (detailsFromDatabase == null)
            return cacheReaderFromFile.readFromFile(sourceName, cacheId);

        return new StringReader(detailsFromDatabase);
    }
}
