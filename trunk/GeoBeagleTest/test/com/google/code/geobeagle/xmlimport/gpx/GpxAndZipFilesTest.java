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

package com.google.code.geobeagle.xmlimport.gpx;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.xmlimport.ImportException;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FilenameFilter;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        File.class, GpxAndZipFiles.class
})
public class GpxAndZipFilesTest {

    private FilenameFilter filenameFilter;
    private GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory;
    private File file;
    private Provider<String> gpxDirProvider;
    private GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter;
    private IGpxReaderIter gpxReaderIter;
    private IGpxReader gpxReader;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        filenameFilter = PowerMock.createMock(FilenameFilter.class);
        gpxFileIterAndZipFileIterFactory = PowerMock
                .createStrictMock(GpxFileIterAndZipFileIterFactory.class);
        file = PowerMock.createMock(File.class);
        gpxDirProvider = PowerMock.createMock(Provider.class);
        gpxFilesAndZipFilesIter = PowerMock.createMock(GpxFilesAndZipFilesIter.class);
        gpxReaderIter = PowerMock.createMock(IGpxReaderIter.class);
        gpxReader = PowerMock.createMock(IGpxReader.class);
    }

    @Test
    public void GpxFilesIterator() throws Exception {
        EasyMock.expect(gpxDirProvider.get()).andReturn("/sdcard/downloads");
        PowerMock.expectNew(File.class, "/sdcard/downloads").andReturn(file);
        String[] fileList = new String[] {
                "foo.gpx", "bar.gpx"
        };
        expect(file.list(filenameFilter)).andReturn(fileList);
        gpxFileIterAndZipFileIterFactory.resetAborter();
        PowerMock.expectNew(GpxFilesAndZipFilesIter.class, fileList,
                gpxFileIterAndZipFileIterFactory).andReturn(gpxFilesAndZipFilesIter);

        PowerMock.replayAll();
        new GpxAndZipFiles(filenameFilter, gpxFileIterAndZipFileIterFactory, gpxDirProvider)
                .iterator();
        PowerMock.verifyAll();
    }

    @Test
    public void GpxFilesIteratorError() throws Exception {
        EasyMock.expect(gpxDirProvider.get()).andReturn("/sdcard/downloads");
        PowerMock.expectNew(File.class, "/sdcard/downloads").andReturn(file);
        expect(file.list(filenameFilter)).andReturn(null);

        PowerMock.replayAll();
        try {
            new GpxAndZipFiles(filenameFilter, gpxFileIterAndZipFileIterFactory, gpxDirProvider)
                    .iterator();
            assertTrue("Should have thrown exception but didn't.", false);
        } catch (ImportException e) {
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testCantReadDir() throws Exception {
        assertFalse(new GpxFilesAndZipFilesIter(new String[] {}, null).hasNext());
    }

    @Test
    public void testFilenameFilter() {
        FilenameFilter filenameFilter = new GpxAndZipFilenameFilter(new GpxFilenameFilter());
        assertTrue(filenameFilter.accept(null, "foo.gpx"));
        assertTrue(filenameFilter.accept(null, "foo.zip"));
        assertTrue(filenameFilter.accept(null, "foo.ZIP"));
        assertFalse(filenameFilter.accept(null, "skip.me"));
        assertFalse(filenameFilter.accept(null, ".appledetritus010.gpx"));
    }

    @Test
    public void testHasSubFiles() throws Exception {
        IGpxReaderIter gpxReaderIter1 = PowerMock.createStrictMock(IGpxReaderIter.class);
        IGpxReaderIter gpxReaderIter2 = PowerMock.createStrictMock(IGpxReaderIter.class);
        IGpxReader gpxReader1 = PowerMock.createStrictMock(IGpxReader.class);
        IGpxReader gpxReader2 = PowerMock.createStrictMock(IGpxReader.class);
        IGpxReader gpxReader3 = PowerMock.createStrictMock(IGpxReader.class);

        expect(gpxFileIterAndZipFileIterFactory.fromFile("foo.zip")).andReturn(gpxReaderIter1);
        expect(gpxReaderIter1.hasNext()).andReturn(true);
        expect(gpxReaderIter1.next()).andReturn(gpxReader1);
        expect(gpxReaderIter1.hasNext()).andReturn(true);
        expect(gpxReaderIter1.next()).andReturn(gpxReader2);
        expect(gpxReaderIter1.hasNext()).andReturn(false);

        expect(gpxFileIterAndZipFileIterFactory.fromFile("bar.gpx")).andReturn(gpxReaderIter2);
        expect(gpxReaderIter2.hasNext()).andReturn(true);
        expect(gpxReaderIter2.next()).andReturn(gpxReader3);
        expect(gpxReaderIter2.hasNext()).andReturn(false);

        PowerMock.replayAll();
        GpxFilesAndZipFilesIter gpxFileIter = new GpxFilesAndZipFilesIter(new String[] {
                "foo.zip", "bar.gpx"
        }, gpxFileIterAndZipFileIterFactory);
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader1, gpxFileIter.next());
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader2, gpxFileIter.next());
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader3, gpxFileIter.next());
        assertFalse(gpxFileIter.hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testNoFiles() throws Exception {
        assertFalse(new GpxFilesAndZipFilesIter(new String[] {}, null).hasNext());
    }

    @Test
    public void testOneFileButEmptySubfile() throws Exception {
        expect(gpxFileIterAndZipFileIterFactory.fromFile("foo.zip")).andReturn(gpxReaderIter);
        expect(gpxReaderIter.hasNext()).andReturn(false);

        PowerMock.replayAll();
        GpxFilesAndZipFilesIter gpxFileIter = new GpxFilesAndZipFilesIter(new String[] {
            "foo.zip"
        }, gpxFileIterAndZipFileIterFactory);
        assertFalse(gpxFileIter.hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testOneFile() throws Exception {
        expect(gpxFileIterAndZipFileIterFactory.fromFile("foo.gpx")).andReturn(gpxReaderIter);
        expect(gpxReaderIter.hasNext()).andReturn(true);
        expect(gpxReaderIter.next()).andReturn(gpxReader);
        expect(gpxReaderIter.hasNext()).andReturn(false);

        PowerMock.replayAll();
        GpxFilesAndZipFilesIter gpxFileIter = new GpxFilesAndZipFilesIter(new String[] {
            "foo.gpx"
        }, gpxFileIterAndZipFileIterFactory);
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader, gpxFileIter.next());
        assertFalse(gpxFileIter.hasNext());
        PowerMock.verifyAll();
    }
}
