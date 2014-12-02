package com.beepscore.android.runtracker;

import android.content.Context;
import android.content.AsyncTaskLoader;

/**
 * Created by stevebaker on 9/29/14.
 * Generic loader for arbitrary data.
 * Does not use a Cursor.
 * Subclasses will implement loadInBackground
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {

    // D is a generic data type
    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

}
