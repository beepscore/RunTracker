package com.beepscore.android.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by stevebaker on 9/28/14.
 */
public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    protected void onLocationReceived(Context context, Location location) {
        RunManager.get(context).insertLocation(location);
    }

}
