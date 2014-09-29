package com.beepscore.android.runtracker;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beepscore.android.runtracker.RunDatabaseHelper.RunCursor;

/**
 * Created by stevebaker on 9/28/14.
 */
public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private static final int REQUEST_NEW_RUN = 0;

    private RunCursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // FIXME: This simple implementation blocks the main thread
        // TODO: Instead use a Loader to load in background
        // Query the list of runs
        mCursor = RunManager.get(getActivity()).queryRuns();

        // Create an adapter to point at this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mCursor);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run:
                Intent intent = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(intent, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_RUN == requestCode) {
            // if user navigates back to this activity, force list to reload
            // FIXME: Don't requery on main thread, use Loader instead
            mCursor.requery();
            ((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // The id argument will be the Run ID; CursorAdapter gives us this for free
        // because we used convention of naming run table column _id
        Intent intent = new Intent(getActivity(), RunActivity.class);
        intent.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(intent);
    }

    // Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // we only ever load the runs, so assume this is the case
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // create an adapter to point at this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunCursor)cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    ////

    private static class RunListCursorLoader extends SQLiteCursorLoader {

        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            // query the list of runs
            return RunManager.get(getContext()).queryRuns();
        }

    }

    private static class RunCursorAdapter extends CursorAdapter {

        private RunCursor mRunCursor;

        private RunCursorAdapter(Context context, RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the run for the current row
            Run run = mRunCursor.getRun();

            // Set up the start date text view
            TextView startDateTextView = (TextView)view;

            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);
        }
    }

}
