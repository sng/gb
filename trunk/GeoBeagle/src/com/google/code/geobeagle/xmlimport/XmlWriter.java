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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class XmlWriter {
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

        public void endCDataText() throws IOException {
            writer.write("]]>");
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
            mLevel = 0;
            new File(new File(path).getParent()).mkdirs();
            writer = writerFactory.create(path);
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        }

        public void startCDataText() throws IOException {
            writer.write("<![CDATA[");
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

        public void text(String text, boolean isCData) throws IOException {
            if (isCData) {
                startCDataText();
                writer.write(text);
                endCDataText();
            } else {
                writer.write(text.replace("<", "&lt;").replace(">", "&gt;"));
            }
        }
    }

    private final FilePathStrategy filePathStrategy;
    private String mFilename;
    private ArrayList<Tag> tagStack;
    private final TagWriter tagWriter;
    private HashMap<String, Integer> textPaths;

    @Inject
    public XmlWriter(FilePathStrategy filePathStrategy, TagWriter tagWriter) {
        this.filePathStrategy = filePathStrategy;
        this.tagWriter = tagWriter;
        textPaths = new HashMap<String, Integer>();
        textPaths.put(EventHandlerGpx.SHORT_DESCRIPTION, 1);
        textPaths.put(EventHandlerGpx.LONG_DESCRIPTION, 1);
        textPaths.put(EventHandlerGpx.LOG_TEXT, 1);
        tagStack = new ArrayList<Tag>();
    }

    public void endTag(String name, String previousFullPath) throws IOException {
        tagWriter.endTag(name);
        if (previousFullPath.equals(EventHandlerGpx.XPATH_WPT)) {
            tagWriter.close();
        }
    }

    public void open(String filename) {
        mFilename = filename;
    }

    public void startTag(String name, HashMap<String, String> attributes) throws IOException {
        Tag tag = new Tag(name, attributes);

        if (tagWriter.isOpen()) {
            tagWriter.startTag(tag);
        } else {
            tagStack.add(tag);
        }
    }

    public void text(String fullPath, String text) throws IOException {
        if (fullPath.equals(EventHandlerGpx.XPATH_WPTNAME)) {
            Iterator<Tag> itrTagStack = tagStack.iterator();
            tagWriter.open(filePathStrategy.getPath(mFilename, text, "gpx"));
            while (itrTagStack.hasNext()) {
                tagWriter.startTag(itrTagStack.next());
            }
        }

        tagWriter.text(text, isText(fullPath));
    }

    private boolean isText(String previousFullPath) {
        return textPaths.containsKey(previousFullPath);
    }
}
