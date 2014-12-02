package com.beepscore.android.runtracker;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

/**
 * Created by stevebaker on 12/1/14.
 */
public class RunMapFragment extends MapFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATIONS = 0;

    private GoogleMap mGoogleMap;
    private LocationCursor mLocationCursor;

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment runMapFragment = new RunMapFragment();
        runMapFragment.setArguments(args);
        return runMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for a Run ID as an argument, and find the run
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(LOAD_LOCATIONS, args, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);

        // Stash a reference to the GoogleMap
        mGoogleMap = getMap();
        // Show the user's location
        mGoogleMap.setMyLocationEnabled(true);

        return view;
    }

    private void updateUI() {
        if (mGoogleMap == null ||
                mLocationCursor == null) {
            return;
        }

        // LatLngBounds for use in zoom to fit
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        PolylineOptions line = locationsLine(mLocationCursor, latLngBuilder);
        // Add the line to the map
        mGoogleMap.addPolyline(line);

        // Zoom map to show the track, with some padding
        // Construct a movement instruction for the map camera
        LatLngBounds latLngBounds = latLngBuilder.build();

        // Use current display size in pixels as a bounding box
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds,
                displayWidth, displayHeight, 15);
        mGoogleMap.moveCamera(movement);
    }

    /**
     * Set up an overlay on the map for this run's locations
     * @return a polyline with all of the points
     */
    private PolylineOptions locationsLine(LocationCursor locationCursor, LatLngBounds.Builder latLngBuilder) {
        PolylineOptions line = new PolylineOptions();

        locationCursor.moveToFirst();
        while (!locationCursor.isAfterLast()) {
            Location location = locationCursor.getLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            Resources resources = getResources();

            if (locationCursor.isFirst()) {
                String startDateString = new Date(location.getTime()).toString();
                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(resources.getString(R.string.run_start))
                        .snippet(resources.getString(R.string.run_started_at_format, startDateString));
                mGoogleMap.addMarker(startMarkerOptions);
            } else if (locationCursor.isLast()) {
                // else so skip if only one location
                String finishDateString = new Date(location.getTime()).toString();
                MarkerOptions finishMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(resources.getString(R.string.run_finish))
                        .snippet(resources.getString(R.string.run_finished_at_format, finishDateString));
                mGoogleMap.addMarker(finishMarkerOptions);
            }

            line.add(latLng);
            latLngBuilder.include(latLng);
            locationCursor.moveToNext();
        }
        return line;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    //////////////////
    // LoaderCallbacks

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mLocationCursor = (LocationCursor)cursor;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the data
        mLocationCursor.close();
        mLocationCursor = null;
    }

    //////////////////

}
