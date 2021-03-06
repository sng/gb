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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.database.DatabaseTest.DesktopSQLiteDatabase;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheWriterTest {

    private static final String INSERT_INTO_CACHES = "INSERT INTO CACHES (Id, Description, Source, DeleteMe) ";
    private static final String INSERT_INTO_GPX = "INSERT INTO GPX (Name, ExportTime, DeleteMe) ";

    private SQLiteWrapper mSqlite;
    private SourceNameTranslator mSourceNameTranslator;
    private GeocacheFactory mFactory;
    private DbFrontend mDbFrontend;

    @Before
    public void setUp() {
        mSqlite = PowerMock.createMock(SQLiteWrapper.class);
        mSourceNameTranslator = PowerMock
                .createMock(SourceNameTranslator.class);
        mFactory = PowerMock.createMock(GeocacheFactory.class);
        mDbFrontend = PowerMock.createMock(DbFrontend.class);
    }

    @Test
    public void testUpdateTag() {
        mDbFrontend.setGeocacheTag("GC123", Tags.ARCHIVED, true);
        
        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        cacheWriterSql.updateTag("GC123", Tags.ARCHIVED, true);
        PowerMock.verifyAll();

    }



    @Test
    public void testIsLockedFromUpdating() {
        EasyMock.expect(mDbFrontend.geocacheHasTag("GC123", Tags.LOCKED_FROM_OVERWRITING)).andReturn(true);
        
        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        assertTrue(cacheWriterSql.isLockedFromUpdating("GC123"));
        PowerMock.verifyAll();

    }
    @Test
    public void testClearCaches() {
        mFactory.flushCache();
        mDbFrontend.flushTotalCount();
        mSqlite.execSQL(Database.SQL_CLEAR_CACHES, "the source");

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        cacheWriterSql.clearCaches("the source");
        PowerMock.verifyAll();
    }

    @Test
    public void testClearEarlierLoads() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(DatabaseTest.currentSchema()); // andpe: Error
        // "table CACHES already exists"

        db.execSQL(INSERT_INTO_CACHES
                + "VALUES ('GCTHISIMPORT', 'just loaded', 'foo.gpx', 0)");
        db.execSQL(INSERT_INTO_CACHES
                + "VALUES ('GCCLICKEDLINK', 'from a link', '"
                + Database.S0_INTENT + "', 0)");
        db.execSQL(INSERT_INTO_CACHES
                + "VALUES ('GCOLDIMPORT', 'from a gpx', 'bar.gpx', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('nuke.gpx', '2009-04-30', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('keep.gpx', '2009-04-30', 0)");

        CacheWriter cacheWriterSql = new CacheWriter(db, null, null, null);
        cacheWriterSql.clearEarlierLoads();

        assertEquals("GCTHISIMPORT|just loaded|||foo.gpx|1|0|0|0|0\n"
                + "GCCLICKEDLINK|from a link|||intent|0|0|0|0|0\n", db
                .dumpTable("CACHES"));
        assertEquals("keep.gpx|2009-04-30|1\n", db.dumpTable("GPX"));
    }

    @Test
    public void testDeleteCache() {
        mFactory.flushGeocache("GC123");
        mDbFrontend.flushTotalCount();
        mSqlite.execSQL(Database.SQL_DELETE_CACHE, "GC123");

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        cacheWriterSql.deleteCache("GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testInsertAndUpdate() {
        EasyMock.expect(mDbFrontend.loadCacheFromId("gc123")).andReturn(null);

        mSqlite.execSQL(Database.SQL_REPLACE_CACHE, "gc123", "a cache", 122.0,
                37.0, "source", 0, 0, 0, 0);
        EasyMock.expect(
                mSourceNameTranslator.sourceTypeToSourceName(Source.GPX,
                        "source")).andReturn("source");
        mFactory.flushGeocache("gc123");

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        assertTrue(cacheWriterSql.conditionallyWriteCache("gc123", "a cache", 122,
                37, Source.GPX, "source", CacheType.NULL, 0, 0, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testInsertAndUpdateAlreadyLoaded() {
        Geocache gcLoaded = new Geocache("gc123", "a cache", 122.0, 37.0,
                Source.GPX, "source", CacheType.NULL, 0, 0, 0, null);
        EasyMock.expect(mDbFrontend.loadCacheFromId("gc123")).andReturn(
                gcLoaded);

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        assertFalse(cacheWriterSql.conditionallyWriteCache("gc123", "a cache",
                122, 37, Source.GPX, "source", CacheType.NULL, 0, 0, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsGpxAlreadyLoadedFalse() {
        expect(
                mSqlite
                        .countResults(Database.TBL_GPX,
                                "Name = ? AND ExportTime >= ?", "foo.gpx",
                                "04-30-2009")).andReturn(0);

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        assertFalse(cacheWriterSql.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsGpxAlreadyLoadedTrue() {
        expect(
                mSqlite.countResults(Database.TBL_GPX,
                        Database.SQL_MATCH_NAME_AND_EXPORTED_LATER, "foo.gpx",
                        "04-30-2009")).andReturn(1);
        mSqlite.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, "foo.gpx");
        mSqlite.execSQL(Database.SQL_GPX_DONT_DELETE_ME, "foo.gpx");

        PowerMock.replayAll();
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend,
                mSourceNameTranslator, mFactory);
        assertTrue(cacheWriterSql.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        PowerMock.verifyAll();
    }

    @Test
    public void testStartWriting() {
        mSqlite.beginTransaction();

        PowerMock.replayAll();
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory)
                .startWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWriting() {
        mSqlite.setTransactionSuccessful();
        mSqlite.endTransaction();

        PowerMock.replayAll();
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory)
                .stopWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testWriteGpx() {
        mSqlite
                .execSQL(Database.SQL_REPLACE_GPX, "foo.gpx",
                        "2009-04-30 10:30");

        PowerMock.replayAll();
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory)
                .writeGpx("foo.gpx", "2009-04-30 10:30");
        PowerMock.verifyAll();
    }
}
