package com.beepscore.android.runtracker;

import java.util.Date;

/**
 * Created by stevebaker on 9/27/14.
 */
public class Run {
    private Date mStartDate;

    public Run() {
        mStartDate = new Date();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getDurationSeconds(long endMillis) {
        return (int) ((endMillis - mStartDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds) {
        // http://forums.bignerdranch.com/viewtopic.php?f=430&t=7659
        int hours = (durationSeconds / 3600);
        int minutes = (durationSeconds / 60) % 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
