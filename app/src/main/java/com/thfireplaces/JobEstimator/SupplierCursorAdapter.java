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
 * {@link SupplierCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of group type data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

class SupplierCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link SupplierCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    SupplierCursorAdapter(Context context, Cursor c) {
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
        return LayoutInflater.from(context).inflate(R.layout.list_item_supplier, parent, false);
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
        TextView tvSupplier = view.findViewById(R.id.supplier_item);
        // Extract properties from cursor
        String supplier = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.SupplierTable.COL_SUPPLIER_NAME));

        // Populate fields with extracted properties
        tvSupplier.setText(supplier);
        int pos = cursor.getPosition();
        if (pos % 2 == 0) {
            view.setBackgroundColor(0xFFE3F2FD);
        } else {
            view.setBackgroundColor(0xFFBBDEFB);
        }
    }
}
