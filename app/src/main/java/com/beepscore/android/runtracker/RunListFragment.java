package com.beepscore.android.runtracker;

import android.app.ListFragment;
import android.os.Bundle;

import com.beepscore.android.runtracker.RunDatabaseHelper.RunCursor;

/**
 * Created by stevebaker on 9/28/14.
 */
public class RunListFragment extends ListFragment {

    private RunCursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FIXME: This simple implementation blocks the main thread
        // TODO: Instead use a Loader to load in background
        // Query the list of runs
        mCursor = RunManager.get(getActivity()).queryRuns();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

}
