package com.beepscore.android.runtracker;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;

/**
 * Created by stevebaker on 12/2/14.
 */
public class LocationCursor extends CursorWrapper {

    public LocationCursor(Cursor cursor) {
        super(cursor);
    }

    public Location getLocation() {
        if (isBeforeFirst() || isAfterLast()) {
            return null;
        }
        // First get the provider
        String provider = getString(getColumnIndex(RunDatabaseHelper.COLUMN_LOCATION_PROVIDER));
        // Use provider to instantiate location
        Location location = new Location(provider);
        // Populate the remaining properties
        location.setLongitude(getDouble(getColumnIndex(RunDatabaseHelper.COLUMN_LOCATION_LONGITUDE)));
        location.setLatitude(getDouble(getColumnIndex(RunDatabaseHelper.COLUMN_LOCATION_LATITUDE)));
        location.setAltitude(getDouble(getColumnIndex(RunDatabaseHelper.COLUMN_LOCATION_ALTITUDE)));
        location.setTime(getLong(getColumnIndex(RunDatabaseHelper.COLUMN_LOCATION_TIMESTAMP)));
        return location;
    }

}

