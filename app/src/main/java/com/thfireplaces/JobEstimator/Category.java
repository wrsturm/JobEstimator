package com.thfireplaces.JobEstimator;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract;

public class Category extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = Category.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    private SupplierCursorAdapter mCategoryCursorAdapter;
    Cursor cursor;
    private Uri mCurrentJobUri;
    private String mSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setTitle(R.string.select_product_category);
        setContentView(R.layout.list_category);
        mCurrentJobUri = getIntent().getData();
        mSelections = getIntent().getStringExtra("selections");

        if (mCurrentJobUri != null) {
            Log.v(LOG_TAG, mCurrentJobUri.toString());
        }

// Find the ListView which will be populated with the customer data
        ListView lvCategory = findViewById(R.id.supplier_list);
        mCategoryCursorAdapter = new SupplierCursorAdapter(this, cursor);
        lvCategory.setAdapter(mCategoryCursorAdapter);
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvCategory = view.findViewById(R.id.supplier_item);
                String category = tvCategory.getText().toString().trim();

                Log.v(LOG_TAG, "Selection:" + category + ":");
                Intent intent = new Intent(Category.this, ProductType.class);
                intent.setData(mCurrentJobUri);
                intent.putExtra("category", category);
                intent.putExtra("selections", mSelections);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderBy = JobEstimatorContract.CategoryTable.COL_CATEGORY;

        return new CursorLoader(this,
                JobEstimatorContract.CategoryTable.CONTENT_URI,
                JobEstimatorContract.CategoryTable.categoryProjection,
                null,
                null,
                orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCategoryCursorAdapter.changeCursor(null);
    }
}
