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

public class ProductType extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ProductType.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    private ProductTypeCursorAdapter mProductTypeCursorAdapter;
    Cursor cursor;
    private Uri mCurrentJobUri;
    private String mSelections;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setTitle(R.string.select_a_model);
        setContentView(R.layout.list_product_type);
        mCurrentJobUri = getIntent().getData();
        mSelections = getIntent().getStringExtra("selections");

        if (mCurrentJobUri != null) {
            Log.v(LOG_TAG, mCurrentJobUri.toString());
        }

// Find the ListView which will be populated with the customer data
        ListView productTypeListView = findViewById(R.id.product_type_list);
        mProductTypeCursorAdapter = new ProductTypeCursorAdapter(this, cursor);
        productTypeListView.setAdapter(mProductTypeCursorAdapter);
        productTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvProductType = view.findViewById(R.id.product_type);
                TextView tvModel = view.findViewById(R.id.product_type_model);
                TextView tvDesc = view.findViewById(R.id.product_type_short_desc);
                String productType = tvProductType.getText().toString().trim();
                String modelGroup = tvModel.getText().toString().trim();
                String desc = tvDesc.getText().toString().trim();

                Log.v(LOG_TAG, "Selection:" + productType + ":" + modelGroup + ":" + desc + ":" + mCategory);
                Intent intent = new Intent(ProductType.this, Product.class);
                intent.setData(mCurrentJobUri);
                intent.putExtra("producttype", productType);
                intent.putExtra("modelgroup", modelGroup);
                intent.putExtra("description", desc);
                intent.putExtra("selections", mSelections);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mCategory = getIntent().getStringExtra("category");
        String select =
                JobEstimatorContract.ProductTypeTable.COL_PT_PRODUCT_TYPE + " =?";
        String[] selectArgs = {mCategory};

        String orderBy = JobEstimatorContract.ProductTypeTable.COL_PT_PRODUCT_TYPE
                + "," + JobEstimatorContract.ProductTypeTable.COL_PT_MODEL;

        return new CursorLoader(this,
                JobEstimatorContract.ProductTypeTable.CONTENT_URI,
                JobEstimatorContract.ProductTypeTable.productTypeProjection,
                select,
                selectArgs,
                orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductTypeCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductTypeCursorAdapter.changeCursor(null);
    }
}
