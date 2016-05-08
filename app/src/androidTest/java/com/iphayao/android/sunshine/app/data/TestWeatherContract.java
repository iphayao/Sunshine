package com.iphayao.android.sunshine.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Phayao on 5/8/2016.
 */
public class TestWeatherContract extends AndroidTestCase {
    private static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 1419033600L;

    public void testBuildWeatherLocation() {
        Uri locationUri = WeatherContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);
        assertNotNull("Error: Null Uri returned. You must fill-in buildWeatherLocation in " +
                "WeatherContract.", locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesnot match our expected resutl",
                locationUri.toString(),
                "content://com.iphayao.android.sunshine.app/weather/%2FNorth%20Pole");
    }

}
