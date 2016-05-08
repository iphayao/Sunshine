package com.iphayao.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Phayao on 5/8/2016.
 */
public class TestDb extends AndroidTestCase{

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    // This function gets called before each test is executed to delete the database. This makes
    // sure that we always have a clean test.
//    public void setUP() {
//        deleteTheDatabase();
//    }

    @Override
    protected void setUp() throws Exception {
        //super.setUp();
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we create the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while(c.moveToNext());
        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables.
        assertTrue("Error: Your database was crated without both the location entry and wather entry tables",
                tableNameHashSet.isEmpty());
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        // if this fails, it mean that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();

    }

    public void testLocationTable() {
        insertLocation();
    }

    public void testWeatherTable() {
        long locationRowId = insertLocation();
        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // error will be thrown here when you try to get a writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Second step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        // Third step: Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        // Verify we got a row back.
        assertTrue(weatherRowId != -1);

        // Forth step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,
                null,   // all columns
                null,   // Column for the "where" clause
                null,   // Values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null    // sort order
        );
        // Move the cursor to a valid database row and check to see if we have any rows
        assertTrue("Error: No records return from weather query", weatherCursor.moveToFirst());

        // Fifth step: Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Weather Query Validation Failed",
                weatherCursor, weatherValues);
        // move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More then one record returned from location query",
                weatherCursor.moveToNext());

        // Sixth step: Close cursor and Database
        weatherCursor.close();
        db.close();
    }

    public long insertLocation() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // error will be thrown here when you try to get a writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Second step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        // Third step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        // Data's inserted. In THEORY. Now pull some to state at it and verify it made.
        // the round trip.

        // Forth step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor locationCursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                null,   // all columns
                null,   // Column for the "where" clause
                null,   // Values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null    // sort order
        );
        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No records return from location query", locationCursor.moveToFirst());

        // Fifth step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                locationCursor, testValues);
        // move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More then one record returned from location query",
                locationCursor.moveToNext());

        // Sixth step: Close cursor and Database
        locationCursor.close();
        db.close();

        return locationRowId;
    }

}
