package com.iphayao.android.sunshine.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iphayao.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.iphayao.android.sunshine.app.data.WeatherContract.WeatherEntry;

/**
 * Created by Phayao on 5/8/2016.
 *
 * Manges a location database for weather data.
 *
 */
public class WeatherDbHelper extends SQLiteOpenHelper{
    // if you change the database schema, you mush increase the database version
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // The ID of the location entry associated with the weather data
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL," +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                WeatherEntry.COLUMN_SHORT_DES + " TEXT NOT NULL," +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +
                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL," +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL," +
                WeatherEntry.COLUMN_DEGREE + " REAL NOT NULL," +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                // To assure the application have just one weather entry pre day
                // per location, it's create a UNIQUE constraint with REPLACE strategy
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +
                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONfLICT REPLACE);";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Not that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);

    }
}
