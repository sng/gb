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
package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.suppressConstructor;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.util.Log;

@PrepareForTest({
        Log.class, TagWriterImpl.class, ContentValues.class
})
@RunWith(PowerMockRunner.class)

public class TagWriterImplTest extends GeoBeagleTest {

    private Provider<ISQLiteDatabase> databaseProvider;
    private DesktopSQLiteDatabase db;
    private Filter filter;
    private TagWriterImpl tagWriterImpl;
    private ContentValues contentValues;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        databaseProvider = createMock(Provider.class);
        filter = createMock(Filter.class);
        db = new DesktopSQLiteDatabase();
        db.execSQL(DatabaseTest.currentSchema());
        tagWriterImpl = new TagWriterImpl(databaseProvider, filter);
        contentValues = createMock(ContentValues.class);
    }

    @Test
    public void testAddDnf() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.isVisible(true)).andReturn(true).anyTimes();
        expect(filter.isVisible(false)).andReturn(true).anyTimes();
        replayAll();

        tagWriterImpl.add("GC123", Tag.DNF);
        assertTrue(tagWriterImpl.hasTag("GC123", Tag.DNF));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.FOUND));
        verifyAll();
    }

    @Test
    public void testAddNotVisible() throws Exception {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.isVisible(true)).andReturn(false).anyTimes();
        suppressConstructor(ContentValues.class);
        PowerMock.expectNew(ContentValues.class).andReturn(contentValues);
        contentValues.put("Visible", 0);
        replayAll();

        tagWriterImpl.add("GC123", Tag.FOUND);
        verifyAll();
    }

    @Test
    public void testAddFound() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.isVisible(true)).andReturn(true).anyTimes();
        expect(filter.isVisible(false)).andReturn(true).anyTimes();
        replayAll();

        tagWriterImpl.add("GC123", Tag.FOUND);
        tagWriterImpl.add("GCabc", Tag.FOUND);
        assertTrue(tagWriterImpl.hasTag("GC123", Tag.FOUND));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.DNF));
        verifyAll();
    }

    @Test
    public void testNoTagsOnStartUp() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.isVisible(true)).andReturn(true).anyTimes();
        expect(filter.isVisible(false)).andReturn(true).anyTimes();

        replayAll();
        TagWriterImpl tagWriterImpl = new TagWriterImpl(databaseProvider, filter);
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.FOUND));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.DNF));
        verifyAll();
    }
}
