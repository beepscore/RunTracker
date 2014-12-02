package com.beepscore.android.runtracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by stevebaker on 12/2/14.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;

    public LocationListCursorLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}
