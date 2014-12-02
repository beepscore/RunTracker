package com.beepscore.android.runtracker;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

/**
 * A convenience class to wrap a cursor that returns rows from the "run" table.
 * The {@link getRun()} method will give you a Run instance representing
 * the current row.
 *
 */
public class RunCursor extends CursorWrapper {

    public RunCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Run object configured for the current row,
     * or null if the current row is invalid.
     */
    public Run getRun() {
        if (isBeforeFirst() || isAfterLast()) {
            return null;
        }
        Run run = new Run();
        long runId = getLong(getColumnIndex(RunDatabaseHelper.COLUMN_RUN_ID));
        run.setId(runId);

        long startDate = getLong(getColumnIndex(RunDatabaseHelper.COLUMN_RUN_START_DATE));
        run.setStartDate(new Date(startDate));

        return run;
    }

}

