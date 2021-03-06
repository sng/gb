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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DatabaseTest extends GeoBeagleTest {

    static String currentSchema() {
        String currentSchema = SQL(Database.SQL_CREATE_CACHE_TABLE_V16)
                + SQL(Database.SQL_CREATE_GPX_TABLE_V10) + SQL(Database.SQL_CREATE_TAGS_TABLE_V12)
                + SQL("CREATE INDEX IDX_DESCRIPTION on CACHES (Description);")
                + SQL(Database.SQL_CREATE_IDX_LATITUDE) + SQL(Database.SQL_CREATE_IDX_LONGITUDE)
                + SQL(Database.SQL_CREATE_IDX_SOURCE) + SQL(Database.SQL_CREATE_IDX_TAGS)
                + SQL("CREATE INDEX IDX_VISIBLE on CACHES (Visible);");
        return currentSchema;
    }

    String schema17 = SQL(Database.SQL_CREATE_CACHE_TABLE_V16)
                + SQL(Database.SQL_CREATE_GPX_TABLE_V10) + SQL(Database.SQL_CREATE_TAGS_TABLE_V12)
                + SQL(Database.SQL_CREATE_IDX_LATITUDE) + SQL(Database.SQL_CREATE_IDX_LONGITUDE)
                + SQL(Database.SQL_CREATE_IDX_SOURCE) + SQL(Database.SQL_CREATE_IDX_TAGS)
                + SQL("CREATE INDEX IDX_VISIBLE on CACHES (Visible);");

    String schema16 = SQL(Database.SQL_CREATE_CACHE_TABLE_V16)
            + SQL(Database.SQL_CREATE_GPX_TABLE_V10) + SQL(Database.SQL_CREATE_TAGS_TABLE_V12)
            + SQL(Database.SQL_CREATE_IDX_LATITUDE) + SQL(Database.SQL_CREATE_IDX_LONGITUDE)
            + SQL(Database.SQL_CREATE_IDX_SOURCE) + SQL(Database.SQL_CREATE_IDX_TAGS);

    String schema13 = SQL(Database.SQL_CREATE_CACHE_TABLE_V13)
            + SQL(Database.SQL_CREATE_GPX_TABLE_V10) + SQL(Database.SQL_CREATE_TAGS_TABLE_V12)
            + SQL(Database.SQL_CREATE_IDX_LATITUDE) + SQL(Database.SQL_CREATE_IDX_LONGITUDE)
            + SQL(Database.SQL_CREATE_IDX_SOURCE) + SQL(Database.SQL_CREATE_IDX_TAGS);

    final static String schema12 = Database.SQL_CREATE_CACHE_TABLE_V11
            + Database.SQL_CREATE_GPX_TABLE_V10 + Database.SQL_CREATE_TAGS_TABLE_V12
            + Database.SQL_CREATE_IDX_LATITUDE + Database.SQL_CREATE_IDX_LONGITUDE
            + Database.SQL_CREATE_IDX_SOURCE + SQL(Database.SQL_CREATE_IDX_TAGS);

    final static String schema11 = Database.SQL_CREATE_CACHE_TABLE_V11
            + Database.SQL_CREATE_GPX_TABLE_V10 + Database.SQL_CREATE_IDX_LATITUDE
            + Database.SQL_CREATE_IDX_LONGITUDE + Database.SQL_CREATE_IDX_SOURCE;

    final static String schema10 = Database.SQL_CREATE_CACHE_TABLE_V10
            + Database.SQL_CREATE_IDX_LATITUDE + Database.SQL_CREATE_IDX_LONGITUDE
            + Database.SQL_CREATE_IDX_SOURCE + Database.SQL_CREATE_GPX_TABLE_V10;

    // Previous schemas.
    final static String schema6 = Database.SQL_CREATE_CACHE_TABLE_V08;

    final static String schema7 = Database.SQL_CREATE_CACHE_TABLE_V08
            + Database.SQL_CREATE_IDX_LATITUDE + Database.SQL_CREATE_IDX_LONGITUDE
            + Database.SQL_CREATE_IDX_SOURCE;

    private static String SQL(String s) {
        return s + "\n";
    }

    @Test
    public void testOnCreate() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onCreate(db);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
    }

    @Test
    public void testUpgradeFrom17() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema17);

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 17);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
    }

    @Test
    public void testUpgradeFrom16() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema16);

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 16);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
    }

    @Test
    public void testUpgradeFrom13() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema13);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");
        db.execSQL("INSERT INTO GPX (Name, ExportTime, DeleteMe) "
                + "VALUES (\"seattle.gpx\", \"2009-06-01\", 1)");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 13);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String caches = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1|0|0|0|0|1|0|1\nGC123||||foo.gpx|1|0|0|0|0|1|0|1\n", caches);
        String gpx = db.dumpTable("GPX");
        assertEquals("seattle.gpx|1970-01-01|1\n", gpx);
    }

    @Test
    public void testUpgradeFrom12() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema12);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");
        db.execSQL("INSERT INTO GPX (Name, ExportTime, DeleteMe) "
                + "VALUES (\"seattle.gpx\", \"2009-06-01\", 1)");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 12);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String caches = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1|0|0|0|0|1|0|1\nGC123||||foo.gpx|1|0|0|0|0|1|0|1\n", caches);
        String gpx = db.dumpTable("GPX");
        assertEquals("seattle.gpx|1970-01-01|1\n", gpx);
    }

    @Test
    public void testUpgradeFrom11() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema11);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");
        db.execSQL("INSERT INTO GPX (Name, ExportTime, DeleteMe) "
                + "VALUES (\"seattle.gpx\", \"2009-06-01\", 1)");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 11);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String caches = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1|0|0|0|0|1|0|1\nGC123||||foo.gpx|1|0|0|0|0|1|0|1\n", caches);
        String gpx = db.dumpTable("GPX");
        assertEquals("seattle.gpx|1970-01-01|1\n", gpx);
    }

    @Test
    public void testUpgradeFrom10() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema10);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");
        db.execSQL("INSERT INTO GPX (Name, ExportTime, DeleteMe) "
                + "VALUES (\"seattle.gpx\", \"2009-06-01\", 1)");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 10);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String caches = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1|0|0|0|0|1|0|1\nGC123||||foo.gpx|1|0|0|0|0|1|0|1\n", caches);
        String gpx = db.dumpTable("GPX");
        assertEquals("seattle.gpx|1970-01-01|1\n", gpx);
    }

    @Test
    public void testUpgradeFrom9() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema7);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 9);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String data = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1|0|0|0|0|1|0|1\nGC123||||foo.gpx|1|0|0|0|0|1|0|1\n", data);
    }

}
