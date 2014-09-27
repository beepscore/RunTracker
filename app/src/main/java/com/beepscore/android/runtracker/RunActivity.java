package com.beepscore.android.runtracker;

import android.support.v4.app.Fragment;


public class RunActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return RunFragment.newInstance();
    }

}
