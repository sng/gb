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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.code.geobeagle.cachedetails.WriterWrapper.WriterFactory;
import com.google.code.geobeagle.xmlimport.XmlWriter.TagWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class XmlWriterTest extends GeoBeagleTest {
    private XmlWriter xmlWriter;
    private TagWriter tagWriter;
    private StringWriterFactory stringWriterFactory;
    private StringWriter stringWriter;
    private XmlPullParserWrapper xmlPullParser;

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
        xmlPullParser = createMock(XmlPullParserWrapper.class);
    }

    @Test
    public void testSimpleTag() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0);

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<name>GC123</name>",
                stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testAttributes() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(2);
        expect(xmlPullParser.getAttributeName(0)).andReturn("lat");
        expect(xmlPullParser.getAttributeValue(0)).andReturn("456");
        expect(xmlPullParser.getAttributeName(1)).andReturn("lon");
        expect(xmlPullParser.getAttributeValue(1)).andReturn("123");

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<name lon='123' lat='456'>GC123</name>", stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testCDataText() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");

        xmlWriter.startTag("groundspeak:short_description", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/groundspeak:cache/groundspeak:short_description",
                "My favorite > cache");
        xmlWriter.endTag("groundspeak:short_description",
                "/gpx/wpt/groundspeak:cache/groundspeak:short_description");
        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<groundspeak:short_description><![CDATA[My favorite > cache]]>"
                + "</groundspeak:short_description>", stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testEscaping() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/name");

        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/name", "foo < > bar");
        xmlWriter.endTag("name", "/gpx/name");

        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<name>foo &lt; &gt; bar</name>", stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testClose() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("name", null, xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/name");

        xmlWriter.startTag("wpt", null, xmlPullParser);
        xmlWriter.endTag("wpt", "/gpx/wpt");

        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<name>GC123</name>\n"
                + "<wpt></wpt>\nEOF\n", stringWriter.toString());
        verifyAll();
    }

}
