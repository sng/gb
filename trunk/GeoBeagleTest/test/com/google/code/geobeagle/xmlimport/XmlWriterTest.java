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

import static org.junit.Assert.*;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.code.geobeagle.cachedetails.WriterWrapper.WriterFactory;
import com.google.code.geobeagle.xmlimport.XmlWriter.TagWriter;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class XmlWriterTest extends GeoBeagleTest {
    private XmlWriter xmlWriter;
    private TagWriter tagWriter;
    private StringWriterFactory stringWriterFactory;
    private StringWriter stringWriter;
    private HashMap<String, String> attributes;

    static class StringWriterFactory implements WriterFactory {
        StringWriter stringWriter;

        public StringWriterFactory(StringWriter stringWriter) {
            this.stringWriter = stringWriter;
        }

        @Override
        public Writer create(String path) throws IOException {
            stringWriter.open(path);
            return stringWriter;
        }
    }

    static class StringWriter implements Writer {
        private java.io.StringWriter stringWriter;

        StringWriter() {
            stringWriter = new java.io.StringWriter();
        }

        @Override
        public void close() throws IOException {
            stringWriter.write("\nEOF\n");
        }

        @Override
        public void open(String path) throws IOException {
            stringWriter.write("FILE: " + path + "\n");
        }

        @Override
        public void write(String str) throws IOException {
            stringWriter.write(str);
        }

        @Override
        public String toString() {
            return stringWriter.toString();
        }
    }

    @Before
    public void setUp() {
        stringWriter = new StringWriter();
        stringWriterFactory = new StringWriterFactory(stringWriter);
        tagWriter = new TagWriter(stringWriterFactory);
        xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
    }

    @Test
    public void testSimpleTag() throws IOException {
        final XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", new HashMap<String, String>());
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<name>GC123</name>",
                stringWriter.toString());
    }

    @Test
    public void testAttributes() throws IOException {
        final XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        attributes = new HashMap<String, String>();
        attributes.put("latitude", "123");
        attributes.put("longitude", "456");
        xmlWriter.startTag("name", attributes);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<name longitude='456' latitude='123'>GC123</name>", stringWriter.toString());
    }

    @Test
    public void testCDataText() throws IOException {
        final XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", new HashMap<String, String>());
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");

        xmlWriter.startTag("groundspeak:short_description", new HashMap<String, String>());
        xmlWriter.text("/gpx/wpt/groundspeak:cache/groundspeak:short_description",
                "My favorite > cache");
        xmlWriter.endTag("groundspeak:short_description",
                "/gpx/wpt/groundspeak:cache/groundspeak:short_description");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<groundspeak:short_description><![CDATA[My favorite > cache]]>"
                + "</groundspeak:short_description>", stringWriter.toString());
    }

    @Test
    public void testEscaping() throws IOException {
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", new HashMap<String, String>());
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/name");

        xmlWriter.startTag("name", new HashMap<String, String>());
        xmlWriter.text("/gpx/name", "foo < > bar");
        xmlWriter.endTag("name", "/gpx/name");

        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<name>foo &lt; &gt; bar</name>", stringWriter.toString());
    }

    @Test
    public void testClose() throws IOException {
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", new HashMap<String, String>());
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/name");

        xmlWriter.startTag("wpt", new HashMap<String, String>());
        xmlWriter.endTag("wpt", "/gpx/wpt");

        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<wpt></wpt>\nEOF\n", stringWriter.toString());
    }

}
