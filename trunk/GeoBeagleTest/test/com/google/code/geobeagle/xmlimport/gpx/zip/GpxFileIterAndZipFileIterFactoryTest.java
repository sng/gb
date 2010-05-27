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

package com.google.code.geobeagle.xmlimport.gpx.zip;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener.GpxFileIter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GpxFileIterAndZipFileIterFactory.class
})
public class GpxFileIterAndZipFileIterFactoryTest {
    private GpxFileOpener gpxFileOpener;
    private GpxFileIter zipFileIterator;
    private Aborter aborter;
    @SuppressWarnings("unchecked")
    private Provider importFolderProvider;
    private ZipFileOpener zipFileOpener;
    private ZipFileIter zipFileIter;
    private ZipInputStreamFactory zipInputStreamFactory;
    private ZipInputFileTester zipInputFileTester;

    @Before
    public void setUp() {
        gpxFileOpener = createStrictMock(GpxFileOpener.class);
        zipFileIterator = createStrictMock(GpxFileIter.class);
        aborter = createMock(Aborter.class);
        importFolderProvider = createMock(Provider.class);
        zipFileOpener = createStrictMock(ZipFileOpener.class);
        zipFileIter = createStrictMock(ZipFileIter.class);
        zipInputStreamFactory = createMock(ZipInputStreamFactory.class);
        zipInputFileTester = createMock(ZipInputFileTester.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFactoryFromGpxFile() throws Exception {
        expect(importFolderProvider.get()).andReturn("/sdcard/download/");
        expectNew(GpxFileOpener.class, "/sdcard/download/foo.gpx", aborter).andReturn(
                gpxFileOpener);
        expect(gpxFileOpener.iterator()).andReturn(zipFileIterator);

        replayAll();
        assertEquals(zipFileIterator, new GpxFileIterAndZipFileIterFactory(null, aborter,
                importFolderProvider).fromFile("foo.gpx"));
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFactoryFromZipFile() throws Exception {
        expect(importFolderProvider.get()).andReturn("/sdcard/download/");
        expectNew(ZipInputStreamFactory.class).andReturn(zipInputStreamFactory);
        expectNew(ZipFileOpener.class, "/sdcard/download/foo.zip", zipInputStreamFactory,
                zipInputFileTester, aborter).andReturn(zipFileOpener);
        expect(zipFileOpener.iterator()).andReturn(zipFileIter);

        replayAll();
        assertEquals(zipFileIter, new GpxFileIterAndZipFileIterFactory(zipInputFileTester, aborter,
                importFolderProvider).fromFile("foo.zip"));
        verifyAll();
    }

    @Test
    public void testFactoryResetAborter() throws Exception {
        aborter.reset();

        replayAll();
        new GpxFileIterAndZipFileIterFactory(null, aborter, null).resetAborter();
        verifyAll();
    }
}
