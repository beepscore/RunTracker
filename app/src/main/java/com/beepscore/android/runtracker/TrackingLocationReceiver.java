package com.beepscore.android.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Created by stevebaker on 9/28/14.
 */
public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    protected void onLocationReceived(Context context, Location location) {
        String TAG = "TrackingLocationReceiver";
        RunManager.get(context).insertLocation(location);
        Log.d(TAG, "latitude " + Double.toString(location.getLatitude()));
        Log.d(TAG, "longitude " + Double.toString(location.getLongitude()));
    }

}
