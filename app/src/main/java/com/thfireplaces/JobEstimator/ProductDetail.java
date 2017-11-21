package com.thfireplaces.JobEstimator;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract.AccessoriesTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.ProductTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.ProductTypeTable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ProductDetail extends AppCompatActivity {
    private static final String LOG_TAG = Product.class.getSimpleName();
    private ArrayList<String> materialCodes;
    private String material;
    private String mSelections;
    private Uri mCurrentJobUri;
    private String productPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_detail);
        mCurrentJobUri = getIntent().getData();
        material = getIntent().getStringExtra("material");
        if (TextUtils.isEmpty(material)) material = "";

        String model = getIntent().getStringExtra("model");
        String lookup = getIntent().getStringExtra("lookup");
        materialCodes = new ArrayList<>();
        get_job_selection();

        CharSequence title = model + " (" + material + ") " + getString(R.string.product_details);
        setTitle(title);

        switch (lookup) {
            case "products":
                get_product_details();
                break;

            case "accessories":
                get_accessories_details();
                break;

            default:
                break;
        }
    }

    private void get_product_details() {

        String description;
        String model, productDescription, price, instructions, supplier, btus, weight, volume;
        TextView tvDetailDescription = findViewById(R.id.detail_product_long_description);
        TextView tvDetailPrice = findViewById(R.id.detail_product_price);
        TextView tvDetailSupplier = findViewById(R.id.detail_supplier);
        TextView tvDetailInstructions = findViewById(R.id.detail_instructions);
        TextView tvDetailBtus = findViewById(R.id.detail_btus);
        TextView tvDetailWeight = findViewById(R.id.detail_weight);
        TextView tvDetailVolume = findViewById(R.id.detail_volume);
        ImageView ivProductPicture = findViewById(R.id.detail_product_picture);

        String select =
                ProductTable.COL_PRODUCT_MATERIAL + " =?";
        String[] selectArgs = {material};
        Cursor productCursor = getContentResolver().query(
                ProductTable.CONTENT_URI,
                ProductTable.productProjection,
                select,
                selectArgs,
                null
        );
        if (productCursor != null) {
            productCursor.moveToFirst();
            model = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_MODEL));
            price = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_PRICE));
            productDescription = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_DESCRIPTION));

            instructions = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_INSTRUCTIONS));
            supplier = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_SUPPLIER));
            btus = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_BTUS));
            weight = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_WEIGHT));
            volume = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_VOLUME));
            productPicture = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_PICTURE));

            description = get_product_description(model);
            if (TextUtils.isEmpty(description)) description = productDescription;

            tvDetailBtus.setText(btus);
            tvDetailDescription.setText(description);
            tvDetailInstructions.setText(instructions);
            tvDetailPrice.setText(price);
            tvDetailSupplier.setText(supplier);
            tvDetailVolume.setText(volume);
            tvDetailWeight.setText(weight);
            String picture = material.trim() + ".jpg";

            // load image
            try {
                // get input stream
                InputStream ims = getAssets().open(picture);
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                ivProductPicture.setImageDrawable(d);
            } catch (IOException ex) {
                Log.v(LOG_TAG, "File not found " + picture);
            }

            // No picture for the material id.  Try if the picture field is set for the product

            if (!TextUtils.isEmpty(productPicture)) {
                String prodPicture = productPicture.trim() + ".jpg";
                try {
                    // get input stream
                    InputStream ims = getAssets().open(prodPicture);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    ivProductPicture.setImageDrawable(d);
                } catch (IOException ex) {
                    Log.v(LOG_TAG, "File not found " + prodPicture);
                }
            }
            productCursor.close();
        }
    }

    private void get_accessories_details() {

        String description;
        String model, productDescription, price, instructions, supplier, btus, weight, volume;
        TextView tvDetailDescription = findViewById(R.id.detail_product_long_description);
        TextView tvDetailPrice = findViewById(R.id.detail_product_price);
        TextView tvDetailSupplier = findViewById(R.id.detail_supplier);
        TextView tvDetailInstructions = findViewById(R.id.detail_instructions);
        TextView tvDetailBtus = findViewById(R.id.detail_btus);
        TextView tvDetailWeight = findViewById(R.id.detail_weight);
        TextView tvDetailVolume = findViewById(R.id.detail_volume);
        ImageView ivProductPicture = findViewById(R.id.detail_product_picture);

        String select =
                AccessoriesTable.COL_ACC_MATERIAL + " =?";
        String[] selectArgs = {material};
        Cursor accessoriesCursor = getContentResolver().query(
                AccessoriesTable.CONTENT_URI,
                AccessoriesTable.accessoriesProjection,
                select,
                selectArgs,
                null
        );
        if (accessoriesCursor != null) {
            accessoriesCursor.moveToFirst();
            model = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_MODEL));
            price = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_PRICE));
            productDescription = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_DESCRIPTION));

            instructions = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_INSTRUCTIONS));
            supplier = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_SUPPLIER));
            btus = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_BTUS));
            weight = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_WEIGHT));
            volume = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_VOLUME));
            productPicture = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_PICTURE));

            description = get_product_description(model);
            if (TextUtils.isEmpty(description)) description = productDescription;

            tvDetailBtus.setText(btus);
            tvDetailDescription.setText(description);
            tvDetailInstructions.setText(instructions);
            tvDetailPrice.setText(price);
            tvDetailSupplier.setText(supplier);
            tvDetailVolume.setText(volume);
            tvDetailWeight.setText(weight);
            String picture = material.trim() + ".jpg";

            // load image
            try {
                // get input stream
                InputStream ims = getAssets().open(picture);
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                ivProductPicture.setImageDrawable(d);
            } catch (IOException ex) {
                Log.v(LOG_TAG, "File not found " + picture);


                // No picture for the material id.  Try if the picture field is set for the product

                if (!TextUtils.isEmpty(productPicture)) {
                    String prodPicture = productPicture.trim() + ".jpg";
                    try {
                        // get input stream
                        InputStream ims = getAssets().open(prodPicture);
                        // load image as Drawable
                        Drawable d = Drawable.createFromStream(ims, null);
                        // set image to ImageView
                        ivProductPicture.setImageDrawable(d);
                    } catch (IOException ex2) {
                        Log.v(LOG_TAG, "Second File not found " + prodPicture);
                    }
                }
            }
            accessoriesCursor.close();
        }
    }

    private String get_product_description(String model) {
        Cursor productTypeCursor;
        String longDescription = null;

        String select =
                ProductTypeTable.COL_PT_MODEL + " =?";
        String[] selectArgs = {model};

        productTypeCursor = getContentResolver().query(
                ProductTypeTable.CONTENT_URI,
                ProductTypeTable.productTypeProjection,
                select,
                selectArgs,
                null
        );
        if (productTypeCursor != null) {
            if (productTypeCursor.moveToFirst()) {
                longDescription = productTypeCursor.getString(productTypeCursor.getColumnIndexOrThrow(ProductTypeTable.COL_PT_LONG_DESC)).trim();
            }
            productTypeCursor.close();
        }
        return longDescription;
    }

    private void get_job_selection() {
        String[] projection = {
                JobTable._ID,
                JobTable.COL_PRODUCT_SELECTIONS
        };
        Cursor jobCursor = getContentResolver().query(
                mCurrentJobUri,
                projection,
                null,
                null,
                null
        );
        if (jobCursor != null) {
            if (jobCursor.moveToFirst()) {
                mSelections = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PRODUCT_SELECTIONS));
                if (!TextUtils.isEmpty(mSelections)) {
                    materialCodes = Utils.decodeEntries(mSelections, ':');
                }
            }
            jobCursor.close();
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
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int pmIndex;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_select:
                get_job_selection();
                pmIndex = materialCodes.indexOf(material);
                if (pmIndex == -1) { //Not already in selection list
                    mSelections += material + ":";
                    update_job_selection();
                }
                finish();
                return true;
            case R.id.action_delete_selection:
                if (materialCodes.size() == 0) {
                    finish();
                    return true;
                }
                pmIndex = materialCodes.indexOf(material);
                if (pmIndex != -1) { //Already in selection list
                    materialCodes.remove(pmIndex);
                }

                StringBuilder selections = new StringBuilder();
                for (int idx = 0; idx < materialCodes.size(); idx++) {
                    selections.append(materialCodes.get(idx));
                    selections.append(":");
                }

                mSelections = selections.toString();
                update_job_selection();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
