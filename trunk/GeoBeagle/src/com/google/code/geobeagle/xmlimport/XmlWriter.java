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

import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.code.geobeagle.cachedetails.WriterWrapper.WriterFactory;
import com.google.inject.Inject;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

class XmlWriter implements EventHandler {
    static class Tag {
        final HashMap<String, String> attributes;
        final String name;

        Tag(String name, HashMap<String, String> attributes) {
            this.name = name;
            this.attributes = attributes;
        }

    }

    static class TagWriter {
        private static final String SPACES = "                        ";
        private int mLevel;
        private Writer writer;
        private final WriterFactory writerFactory;

        @Inject
        public TagWriter(WriterFactory writerFactory) {
            this.writer = null;
            this.writerFactory = writerFactory;
        }

        public void close() throws IOException {
            writer.close();
            writer = null;
        }

        public void endTag(String name) throws IOException {
            mLevel--;
            if (writer != null)
                writer.write("</" + name + ">");
        }

        public boolean isOpen() {
            return writer != null;
        }

        public void open(String path) throws IOException {
            Log.d("GeoBeagle", "OPENING: " + path);
            mLevel = 0;
            new File(new File(path).getParent()).mkdirs();
            writer = writerFactory.create(path);
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        }

        public void startTag(Tag tag) throws IOException {
            writeNewline();
            mLevel++;
            writer.write("<" + tag.name);
            for (String key : tag.attributes.keySet()) {
                writer.write(" " + key + "='" + tag.attributes.get(key) + "'");
            }
            writer.write(">");
        }

        private void writeNewline() throws IOException {
            writer.write("\n" + SPACES.substring(0, Math.min(mLevel, SPACES.length())));
        }

        public void text(String text) throws IOException {
            writer.write(text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
        }
    }

    private final FilePathStrategy filePathStrategy;
    private String filename;
    private final TagWriter tagWriter;
    private Tag tagWpt;

    @Inject
    public XmlWriter(FilePathStrategy filePathStrategy, TagWriter tagWriter) {
        this.filePathStrategy = filePathStrategy;
        this.tagWriter = tagWriter;
    }

    public void endTag(String name, String previousFullPath) throws IOException {
        if (!previousFullPath.startsWith("/gpx/wpt"))
            return;

        if (tagWriter.isOpen())
            tagWriter.endTag(name);

        if (previousFullPath.equals(EventHandlerGpx.XPATH_WPT)) {
            tagWriter.endTag("gpx");
            tagWriter.close();
        }
    }

    @Override
    public void open(String filename) {
        this.filename = filename;
    }

    @Override
    public void startTag(String name, String fullPath, XmlPullParserWrapper xmlPullParser)
            throws IOException {
        if (!fullPath.startsWith("/gpx/wpt"))
            return;

        Log.d("GeoBeagle", "start tag: " + fullPath);

        HashMap<String, String> attributes = new HashMap<String, String>();

        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            attributes.put(xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
        }
        Tag tag = new Tag(name, attributes);

        if (fullPath.equals("/gpx/wpt")) {
            tagWpt = tag;
        } else if (tagWriter.isOpen()) {
            tagWriter.startTag(tag);
        }
    }

    public boolean text(String fullPath, String text) throws IOException {
        if (!fullPath.startsWith("/gpx/wpt"))
            return true;

        if (text.trim().length() == 0)
            return true;

        Log.d("GeoBeagle", "xmlwriter: " + fullPath);
        if (fullPath.equals(EventHandlerGpx.XPATH_WPTNAME)) {
            tagWriter.open(filePathStrategy.getPath(filename, text, "gpx"));
            tagWriter.startTag(new Tag("gpx", new HashMap<String, String>()));
            tagWriter.startTag(tagWpt);
            tagWriter.startTag(new Tag("name", new HashMap<String, String>()));
        }

        if (tagWriter.isOpen())
            tagWriter.text(text);
        return true;
    }
}
