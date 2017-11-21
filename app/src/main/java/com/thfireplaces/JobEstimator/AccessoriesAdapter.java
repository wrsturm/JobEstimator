package com.thfireplaces.JobEstimator;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * {@link AccessoriesAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product type data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */

public class AccessoriesAdapter extends ArrayAdapter<Accessory> {
    /**
     * Constructs a new {@link AccessoriesAdapter}.
     *
     * @param context The context
     * @param accessories     The row data for current list.
     */
    AccessoriesAdapter(Context context, ArrayList<Accessory> accessories) {
        super(context, 0, accessories /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param position    position in list
     * @param convertView The view at the current position
     * @param parent      The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View lvAccessories = convertView;
        if (lvAccessories == null) {
            lvAccessories = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_accessories, parent, false);
        }
        String format = "%8.2f";

        Accessory currentItem = getItem(position);
        assert currentItem != null;
        String material = currentItem.getMaterial();
        String productName = currentItem.getProductName();
        String description = currentItem.getDescription();
        String unitPrice = currentItem.getUnitPrice();

        TextView tvMaterial = lvAccessories.findViewById(R.id.accessories_material);
        TextView tvDescription = lvAccessories.findViewById(R.id.accessories_description);
        TextView tvProductName = lvAccessories.findViewById(R.id.accessories_name);
        TextView tvUnitPrice = lvAccessories.findViewById(R.id.accessories_price);

        // Populate fields with extracted properties
        tvMaterial.setText(material);
        tvProductName.setText(productName);
        tvDescription.setText(description);
        try {
            tvUnitPrice.setText(String.format(Locale.CANADA, format, Double.valueOf(unitPrice)));
        } catch (NumberFormatException ex) {
            tvUnitPrice.setText(unitPrice);
        }

        int pos = getPosition(currentItem);
        if (pos % 2 == 0) {
            lvAccessories.setBackgroundColor(0xFFE3F2FD);
        } else {
            lvAccessories.setBackgroundColor(0xFFBBDEFB);
        }
        return lvAccessories;
    }
}
