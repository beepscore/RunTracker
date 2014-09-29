package com.beepscore.android.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.beepscore.android.runtracker.RunDatabaseHelper.LocationCursor;
import com.beepscore.android.runtracker.RunDatabaseHelper.RunCursor;

/**
 * Created by stevebaker on 9/27/14.
 * RunManager is a singleton.
 */
public class RunManager {
    private static final String TAG = "RunManager";

    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

    public static final String ACTION_LOCATION = "com.beepscore.android.runtracker.ACTION_LOCATION";

    private static final String TEST_PROVIDER = "TEST_PROVIDER";

    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mDatabaseHelper;
    private SharedPreferences mPreferences;
    private long mCurrentRunId;

    // The private constructor forces users to use RunManager.get(Context)
    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mDatabaseHelper = new RunDatabaseHelper(mAppContext);
        mPreferences = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPreferences.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager get(Context context) {
        if (sRunManager == null) {
            // Use the application context to avoid leaking activities
            sRunManager = new RunManager(context.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        // If you have the test provider and it's enabled, use it
        if (mLocationManager.getProvider(TEST_PROVIDER) != null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);

        // Get the last known location and broadcast it if you have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // Reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        // Start updates from the location manager
        PendingIntent pendingIntent = getLocationPendingIntent(true);
        long MIN_TIME = 10000;
        mLocationManager.requestLocationUpdates(provider, MIN_TIME, 0, pendingIntent);
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public void stopLocationUpdates() {
        PendingIntent pendingIntent = getLocationPendingIntent(false);
        if (pendingIntent != null) {
            mLocationManager.removeUpdates(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingRun(Run run) {
        return ((run != null) && (run.getId() == mCurrentRunId));
    }

    public Run startNewRun() {
        // Insert a run into the db
        Run run = insertRun();
        // Start tracking the run
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run) {
        // Keep the ID
        mCurrentRunId = run.getId();
        // Store it in shared preferences
        mPreferences.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
        // Start location updates
        startLocationUpdates();
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPreferences.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mDatabaseHelper.insertRun(run));
        return run;
    }

    public RunCursor queryRuns() {
        return mDatabaseHelper.queryRuns();
    }

    public Run getRun(long id) {
        Run run = null;
        RunCursor cursor = mDatabaseHelper.queryRun(id);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast()) {
            run = cursor.getRun();
        }
        // close releases memory used by cursor
        cursor.close();
        return run;
    }

    public void insertLocation(Location location) {
        if (mCurrentRunId != -1) {
            mDatabaseHelper.insertLocation(mCurrentRunId, location);
        } else {
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }

    public Location getLastLocationForRun(long runId) {
        Location location = null;
        LocationCursor cursor = mDatabaseHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        // if we got a row, get a location
        if (!cursor.isAfterLast()) {
            location = cursor.getLocation();
        }
        cursor.close();
        return location;
    }

}
