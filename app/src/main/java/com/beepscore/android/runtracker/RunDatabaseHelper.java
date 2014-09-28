package com.beepscore.android.runtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stevebaker on 9/28/14.
 */
public class RunDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "runs.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_RUN = "run";
    private static final String COLUMN_RUN_START_DATE = "start_date";

    public RunDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "run" table
        db.execSQL("create table run (" +
        "_id integer primary key autoincrement, start_date integer)");
        db.execSQL("create table location (" +
                " timestamp integer, latitude real, longitude real, altitude real," +
                " provider varchar(100), run_id integer references run(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement database migration e.g. schema change when upgrading.
        // Expects version to start at 1 and increment by 1 each time (is this required?)
    }

    /**
     * @return run id
     */
    public long insertRun(Run run) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
        // CursorFactory argument null
        return getWritableDatabase().insert(TABLE_RUN, null, contentValues);
    }

}
