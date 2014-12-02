package com.beepscore.android.runtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by stevebaker on 12/1/14.
 */
public class RunMapFragment extends MapFragment {
    private static final String ARG_RUN_ID = "RUN_ID";

    private GoogleMap mGoogleMap;

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment runMapFragment = new RunMapFragment();
        runMapFragment.setArguments(args);
        return runMapFragment;
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

}
