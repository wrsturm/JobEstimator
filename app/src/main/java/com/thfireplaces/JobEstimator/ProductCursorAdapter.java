package com.thfireplaces.JobEstimator;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract;

import java.util.Locale;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product type data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */

class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name, supplier, price and weblink for the current product can be set on the TextViews
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameView = view.findViewById(R.id.product_name);
        TextView productMaterialView = view.findViewById(R.id.product_material);
        TextView descriptionView = view.findViewById(R.id.product_description);
        TextView supplierView = view.findViewById(R.id.product_supplier);
        TextView priceView = view.findViewById(R.id.product_price);
        // Extract properties from cursor
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTable.COL_PRODUCT_MODEL));
        String productMaterial = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTable.COL_PRODUCT_MATERIAL));
        String productDescription = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTable.COL_PRODUCT_DESCRIPTION));
        String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTable.COL_PRODUCT_PRICE));
        String productSupplier = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTable.COL_PRODUCT_SUPPLIER));

            if (TextUtils.isEmpty(productPrice)) {
                productPrice = "N/A";
            }
        String format = "%8.2f";

        // Populate fields with extracted properties
        nameView.setText(productName);
        productMaterialView.setText(productMaterial);
        descriptionView.setText(productDescription);
        supplierView.setText(productSupplier);
        try {
            priceView.setText(String.format(Locale.CANADA, format, Double.valueOf(productPrice)));
        } catch (NumberFormatException ex) {
            priceView.setText(productPrice);
        }
        int pos = cursor.getPosition();
        if (pos % 2 == 0) {
            view.setBackgroundColor(0xFFE3F2FD);
        } else {
            view.setBackgroundColor(0xFFBBDEFB);
        }
    }
}
