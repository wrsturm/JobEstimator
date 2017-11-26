package com.thfireplaces.JobEstimator;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract.AccessoriesTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.ProductTable;

import java.util.ArrayList;

public class Product extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = Product.class.getSimpleName();
    private static final int JOB_QUOTE_LOADER = 0;
    private ProductCursorAdapter mProductCursorAdapter;
    private AccessoriesAdapter mAccessoriesAdapter;
    private ArrayList<Accessory> accessoriesList;
    private ArrayList<String> materialCodes;
    private final Cursor cursor = null;
    private String model;
    private String mSelections;
    private Uri mCurrentJobUri;
    private TextView tvSelections;
    private Uri mStateJobUri;
    private String mStateModel;
    private String mStateSupplier;
    private String productMaterial;
    private String mSupplier;
    private String lastSeen = "";
    private TextView accHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_product);

        mCurrentJobUri = getIntent().getData();
        String desc = getIntent().getStringExtra("description");
        mSupplier = getIntent().getStringExtra("supplier");

        CharSequence title = mSupplier + " " + getString(R.string.product_selection);
        setTitle(title);
        accessoriesList = new ArrayList<>();
        materialCodes = new ArrayList<>();
        get_job_selection();
        if (!TextUtils.isEmpty(mSelections)) {
            materialCodes = Utils.decodeEntries(mSelections, ':');
        } else {
            materialCodes.clear();
        }
        if (mCurrentJobUri != null) {
            Log.v(LOG_TAG, "URI:" + mCurrentJobUri.toString() + " selection:" + mSupplier + ":"
                    + ":" + desc);
        }


// Find the ListView which will be populated with the customer data
        final ListView lvProduct = findViewById(R.id.product_list);
        final ListView lvAccessories = findViewById(R.id.accessories_list);
        mAccessoriesAdapter = new AccessoriesAdapter(this, accessoriesList);
        lvAccessories.setAdapter(mAccessoriesAdapter);
        accHeader = findViewById(R.id.accessories_header);

        accHeader.setText(getString(R.string.accessories));

        tvSelections = findViewById(R.id.selections_view);
        mProductCursorAdapter = new ProductCursorAdapter(this, cursor);
        lvProduct.setAdapter(mProductCursorAdapter);
        String displaySelection = materialCodes.toString();
        if (displaySelection.equals("[]")) displaySelection = getString(R.string.no_selection);
        tvSelections.setText(displaySelection);

// short click
// to select item
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvProductMaterial = view.findViewById(R.id.product_material);
                TextView tvName = view.findViewById(R.id.product_name);
                String productName = (String) tvName.getText();
                String accText = getString(R.string.accessories_title);
                productMaterial = (String) tvProductMaterial.getText();
                //accText.append(getString(R.string.accessories_title));
                //accText.append(productName);
                //accText.append(productMaterial);

                accHeader.setText(String.format(accText, productName, productMaterial));
                fillAccessoriesList();

                mAccessoriesAdapter.notifyDataSetChanged();

                if (lastSeen.equals(productMaterial)) {
                    if (materialCodes.indexOf(productMaterial) == -1) {
                        showSelectConfirmationDialog();
                        lastSeen = "";
                    }
                }
                lastSeen = productMaterial;
            }

        });
// Long click to go to the Product Details
        lvProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvProductName = view.findViewById(R.id.product_name);
                TextView tvProductMaterial = view.findViewById(R.id.product_material);
                //TextView tvProductDescription = (TextView) v.findViewById(R.id.product_description);
                //TextView tvSupplier = (TextView) v.findViewById(R.id.product_supplier);
                //TextView tvPrice = (TextView) v.findViewById(R.id.product_price);

                model = (String) tvProductName.getText();
                productMaterial = (String) tvProductMaterial.getText();
                //String productDescription = (String) tvProductDescription.getText();
                //String supplier = (String) tvSupplier.getText();
                //String price = (String) tvPrice.getText();
                Intent intent = new Intent(Product.this, ProductDetail.class);
                intent.putExtra("material", productMaterial);
                intent.putExtra("model", model);
                intent.putExtra("lookup", "products");

                if (mCurrentJobUri != null) {
                    Log.v(LOG_TAG, mCurrentJobUri.toString());
                    intent.setData(mCurrentJobUri);
                    startActivity(intent);
                }

                return true;
            }
        });
// short click
// to select item
        lvAccessories.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvProductMaterial = view.findViewById(R.id.accessories_material);
                productMaterial = (String) tvProductMaterial.getText();
                if (materialCodes.indexOf(productMaterial) == -1) {
                    showSelectConfirmationDialog();
                }
            }
        });
// Long click to go to the Product Details
        lvAccessories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvProductName = view.findViewById(R.id.accessories_name);
                TextView tvProductMaterial = view.findViewById(R.id.accessories_material);
                //TextView tvProductDescription = (TextView) v.findViewById(R.id.product_description);
                //TextView tvSupplier = (TextView) v.findViewById(R.id.product_supplier);
                //TextView tvPrice = (TextView) v.findViewById(R.id.product_price);

                model = (String) tvProductName.getText();
                productMaterial = (String) tvProductMaterial.getText();
                //String productDescription = (String) tvProductDescription.getText();
                //String supplier = (String) tvSupplier.getText();
                //String price = (String) tvPrice.getText();
                Intent intent = new Intent(Product.this, ProductDetail.class);
                intent.putExtra("material", productMaterial);
                intent.putExtra("model", model);
                intent.putExtra("lookup", "accessories");

                if (mCurrentJobUri != null) {
                    Log.v(LOG_TAG, mCurrentJobUri.toString());
                    intent.setData(mCurrentJobUri);
                    startActivity(intent);
                }

                return true;
            }
        });

        getLoaderManager().initLoader(JOB_QUOTE_LOADER, null, this);
    }

    private void fillAccessoriesList() {
        String acc = null;
        accessoriesList.clear();
        String[] projection = {ProductTable._ID,
                ProductTable.COL_PRODUCT_ACCESSORIES};
        String select = ProductTable.COL_PRODUCT_MATERIAL + " =?";
        String[] selectArgs = {productMaterial};
        Cursor cursor = getContentResolver().query(
                ProductTable.CONTENT_URI,
                projection,
                select,
                selectArgs,
                null); //sort
        if (cursor != null) {
            if (cursor.moveToNext()) {
                acc = cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_ACCESSORIES));
            }
            cursor.close();
        }
        if (acc != null) {
            if (acc.length() != 0) {
                Cursor accCursor;
                ArrayList<String> accStage;
                accStage = Utils.decodeEntries(acc, ':');

                String[] project = {
                        AccessoriesTable._ID,
                        AccessoriesTable.COL_ACC_MATERIAL,
                        AccessoriesTable.COL_ACC_MODEL,
                        AccessoriesTable.COL_ACC_DESCRIPTION,
                        AccessoriesTable.COL_ACC_PRICE,
                };

                selectArgs = accStage.toArray(selectArgs);

                int count = selectArgs.length;

                StringBuilder paramList = new StringBuilder("?");
                for (int idx = 1; idx < count; idx++) paramList.append(",?");

                select = AccessoriesTable.COL_ACC_MATERIAL + " IN (" + paramList.toString() + ")";

                accCursor = getContentResolver().query(
                        AccessoriesTable.CONTENT_URI,
                        project,
                        select,
                        selectArgs,
                        null
                );
                if (accCursor != null) {
                    while (accCursor.moveToNext()) {
                        accessoriesList.add(new Accessory(accCursor.getString(accCursor.getColumnIndex(AccessoriesTable.COL_ACC_MATERIAL)),
                                accCursor.getString(accCursor.getColumnIndex(AccessoriesTable.COL_ACC_MODEL)),
                                accCursor.getString(accCursor.getColumnIndex(AccessoriesTable.COL_ACC_DESCRIPTION)),
                                accCursor.getString(accCursor.getColumnIndex(AccessoriesTable.COL_ACC_PRICE)))
                        );
                    }
                    accCursor.close();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save_selection:
                save_job_selections();
                return true;
            case R.id.action_delete_selection:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_view_estimate:
                viewEstimate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save_job_selections() {
        if (mCurrentJobUri != null) {
            StringBuilder selections = new StringBuilder();

            for (int i = 0; i < materialCodes.size(); i++) {
                selections.append(materialCodes.get(i));
                selections.append(":");
            }

            ContentValues values = new ContentValues();

            values.put(JobTable.COL_PRODUCT_SELECTIONS, selections.toString());

            int rows = getContentResolver().update(mCurrentJobUri,
                    values,
                    null,
                    null);
            if (rows == 0) {
                Toast.makeText(this, "Update job with selections failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update job with selections successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void viewEstimate() {
        if (mCurrentJobUri != null) {
            Intent intent = new Intent(Product.this, ViewEstimate.class);
            Log.v(LOG_TAG, mCurrentJobUri.toString());
            intent.setData(mCurrentJobUri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Job is not valid for action", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        if (materialCodes.size() == 0) return;
        StringBuilder selections = new StringBuilder();
        for (int idx = 0; idx < materialCodes.size(); idx++) {
            selections.append(materialCodes.get(idx));
            selections.append(":");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete) + " " + selections.toString());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product selections.
                deleteSelections();
                get_job_selection();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product selections.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSelectConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.select) + " " + productMaterial);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product selections.
                addToSelections();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product selections.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addToSelections() {
        get_job_selection();
        int pmIndex = materialCodes.indexOf(productMaterial);
        if (pmIndex == -1) { //Not already in selection list
            mSelections += productMaterial + ":";
            materialCodes.add(productMaterial);
            update_job_selection();
            get_job_selection();
            String displaySelection = materialCodes.toString();
            if (displaySelection.equals("[]")) displaySelection = getString(R.string.no_selection);
            tvSelections.setText(displaySelection);
        }
    }

    private void update_job_selection() {
        if (TextUtils.isEmpty(mSelections)) mSelections = "";
        ContentValues values = new ContentValues();
        values.put(JobTable.COL_PRODUCT_SELECTIONS, mSelections);
        int rows = getContentResolver().update(
                mCurrentJobUri,
                values,
                null,
                null
        );
        if (rows == 0) {
            Toast.makeText(this, "Update job with selections failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update job with selections successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelections() {
        ContentValues values = new ContentValues();

        values.put(JobTable.COL_PRODUCT_SELECTIONS, "");

        int rows = getContentResolver().update(mCurrentJobUri,
                values,
                null,
                null);
        if (rows == 0) {
            Toast.makeText(this, "Update job with selections failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update job with selections successful", Toast.LENGTH_SHORT).show();
        }
        tvSelections.setText(R.string.no_selection);
    }

    private void get_job_selection() {
        Cursor jobCursor;
        String[] projection = {
                JobTable._ID,
                JobTable.COL_PRODUCT_SELECTIONS
        };
        jobCursor = getContentResolver().query(
                mCurrentJobUri,
                projection,
                null,
                null,
                null
        );
        if (jobCursor != null) {
            if (jobCursor.moveToFirst()) {
                mSelections = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PRODUCT_SELECTIONS));
                if (!TextUtils.isEmpty(mSelections)) mSelections = mSelections.trim();

                materialCodes.clear();
                if (!TextUtils.isEmpty(mSelections)) {
                    materialCodes = Utils.decodeEntries(mSelections, ':');
                } else {
                    mSelections = "";
                }
            }
            jobCursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductTable._ID,
                ProductTable.COL_PRODUCT_PARENT,
                ProductTable.COL_PRODUCT_MATERIAL,
                ProductTable.COL_PRODUCT_MODEL,
                ProductTable.COL_PRODUCT_DESCRIPTION,
                ProductTable.COL_PRODUCT_PRICE,
                ProductTable.COL_PRODUCT_INSTRUCTIONS,
                ProductTable.COL_PRODUCT_SUPPLIER,
                ProductTable.COL_PRODUCT_BTUS,
                ProductTable.COL_PRODUCT_WEIGHT,
                ProductTable.COL_PRODUCT_VOLUME,
                ProductTable.COL_PRODUCT_ACCESSORIES
        };
        String select =
                ProductTable.COL_PRODUCT_SUPPLIER + " LIKE ?";
        String[] selectArgs = {"%" + mSupplier + "%"};
        String order = ProductTable.COL_PRODUCT_MODEL;

        return new CursorLoader(this,
                ProductTable.CONTENT_URI,
                projection,
                select,
                selectArgs,
                order); //sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mProductCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.changeCursor(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStateJobUri = mCurrentJobUri;
        mStateSupplier = mSupplier;
        mStateModel = model;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurrentJobUri = mStateJobUri;
        mSupplier = mStateSupplier;
        model = mStateModel;
        CharSequence title = mSupplier + " " + getString(R.string.product_selection);
        setTitle(title);
        get_job_selection();
        Log.v(LOG_TAG, "onRestart" + mCurrentJobUri + ":" +
                model + ":" +
                mSelections);

        String displaySelection = materialCodes.toString();
        if (displaySelection.equals("[]")) displaySelection = getString(R.string.no_selection);
        tvSelections.setText(displaySelection);
    }
}
