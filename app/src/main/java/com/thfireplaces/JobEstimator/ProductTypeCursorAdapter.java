package com.thfireplaces.JobEstimator;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract;
/**
 * {@link ProductTypeCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product type data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class ProductTypeCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link JobEstimatorCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    ProductTypeCursorAdapter(Context context, Cursor c) {
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
        return LayoutInflater.from(context).inflate(R.layout.list_item_product_type, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
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
        TextView productTypeView = view.findViewById(R.id.product_type);
        TextView productSupplierView = view.findViewById(R.id.product_type_model);
        TextView productNameView = view.findViewById(R.id.product_type_short_desc);
        // Extract properties from cursor
        String productType = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTypeTable.COL_PT_PRODUCT_TYPE));
        String productModel = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTypeTable.COL_PT_MODEL));
        String productDesc = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.ProductTypeTable.COL_PT_SHORT_DESC));

        // Populate fields with extracted properties
        productTypeView.setText(productType);
        productSupplierView.setText(productModel);
        productNameView.setText(productDesc);
        int pos = cursor.getPosition();
        if (pos % 2 == 0) {
            view.setBackgroundColor(0xFFE3F2FD);
        } else {
            view.setBackgroundColor(0xFFBBDEFB);
        }
    }
}
