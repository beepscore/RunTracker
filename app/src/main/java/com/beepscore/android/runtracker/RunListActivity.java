package com.beepscore.android.runtracker;

import android.app.Fragment;

/**
 * Created by stevebaker on 9/28/14.
 */
public class RunListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }

}
