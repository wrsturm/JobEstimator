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


public class Supplier extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = Supplier.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    private SupplierCursorAdapter mSupplierCursorAdapter;
    Cursor cursor;
    private Uri mCurrentJobUri;
    private String mSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setTitle(R.string.supplier_selection);
        setContentView(R.layout.list_supplier);
        mCurrentJobUri = getIntent().getData();
        mSelections = getIntent().getStringExtra("selections");
        if (mCurrentJobUri != null) {
            Log.v(LOG_TAG, mCurrentJobUri.toString());
        }

// Find the ListView which will be populated with the customer data
        final ListView lvSupplier = findViewById(R.id.supplier_list);
        mSupplierCursorAdapter = new SupplierCursorAdapter(this, cursor);
        lvSupplier.setAdapter(mSupplierCursorAdapter);
        lvSupplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSupplier = view.findViewById(R.id.supplier_item);
                String supplier = tvSupplier.getText().toString().trim();

                Log.v(LOG_TAG, "Selection:" + supplier + ":");
                Intent intent = new Intent(Supplier.this, Product.class);
                intent.setData(mCurrentJobUri);
                intent.putExtra("supplier", supplier);
                intent.putExtra("selections", mSelections);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       String orderBy = JobEstimatorContract.SupplierTable.COL_SUPPLIER_NAME;

        return new CursorLoader(this,
                JobEstimatorContract.SupplierTable.CONTENT_URI,
                JobEstimatorContract.SupplierTable.supplierProjection,
                null,
                null,
                orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSupplierCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSupplierCursorAdapter.changeCursor(null);
    }
}
