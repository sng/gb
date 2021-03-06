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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@PrepareForTest( {
        FileDataVersionChecker.class, Log.class
})
@RunWith(PowerMockRunner.class)
public class FileDataVersionCheckerTest extends GeoBeagleTest {
    @Test
    public void testNeedsUpdate() throws Exception {
        GeoBeagleEnvironment geoBeagleEnvironment = createMock(GeoBeagleEnvironment.class);
        expect(geoBeagleEnvironment.getVersionPath()).andReturn("/sdcard/download/VERSION");
        File file = createMock(File.class);
        expectNew(File.class, "/sdcard/download/VERSION").andReturn(file);
        expect(file.exists()).andReturn(true);
        FileReader fileReader = createMock(FileReader.class);
        expectNew(FileReader.class, file).andReturn(fileReader);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        expectNew(BufferedReader.class, fileReader).andReturn(bufferedReader);
        expect(bufferedReader.readLine()).andReturn("<");
        expect(Log.e((String)EasyMock.anyObject(), (String)EasyMock.anyObject())).andReturn(0);

        replayAll();
        FileDataVersionChecker fileDataVersionChecker = new FileDataVersionChecker(
                geoBeagleEnvironment);
        assertEquals(true, fileDataVersionChecker.needsUpdating());
        verifyAll();
    }
}
