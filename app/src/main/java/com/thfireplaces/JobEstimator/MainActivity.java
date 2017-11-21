package com.thfireplaces.JobEstimator;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    //private static String PACKAGE_NAME;
    private JobEstimatorCursorAdapter mJobCursorAdapter;
    private final Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        //PACKAGE_NAME = getApplicationContext().getPackageName();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String serPrefix = sharedPref.getString("username", "JobEstimator").trim();
// TODO setup input for username at startup.
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditJob.class);
                startActivity(intent);
            }
        });
        Log.v(LOG_TAG, "SerPrefix:" + serPrefix);

        // Find the ListView which will be populated with the customer data
        ListView openingListView = findViewById(R.id.job_list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        openingListView.setEmptyView(emptyView);
        mJobCursorAdapter = new JobEstimatorCursorAdapter(this, cursor);
        openingListView.setAdapter(mJobCursorAdapter);
        openingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditJob.class);
                Uri uri = ContentUris.withAppendedId(JobTable.CONTENT_URI, id);
                Log.v(LOG_TAG, uri.toString());
                intent.setData(uri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opening_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Don't need all the columns here
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                JobTable.CONTENT_URI,
                JobTable.jobProjection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mJobCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJobCursorAdapter.changeCursor(null);
    }
}

