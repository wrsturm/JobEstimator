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
 * {@link ViewEstimateItemAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product type data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */

public class ViewEstimateItemAdapter extends ArrayAdapter<Item> {

    //private final String LOG_TAG = ViewEstimateItemAdapter.class.getSimpleName();
    //String quant = (String) tvQuantity.getText();
    //double linePrice, unitPrice;
    //int quantity;

    /**
     * Constructs a new {@link ViewEstimateItemAdapter}.
     *
     * @param context The context
     * @param items   The row data for current list.
     */
    ViewEstimateItemAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items /* flags */);
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
        View lvEstimate = convertView;
        if (lvEstimate == null) {
            lvEstimate = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_estimate, parent, false);
        }
        String format = "%8.2f";

        Item currentItem = getItem(position);
        assert currentItem != null;
        String quantity = currentItem.getQuantity();
        String material = currentItem.getMaterial();
        String productName = currentItem.getProductName();
        String description = currentItem.getDescription();
        String unitPrice = currentItem.getUnitPrice();
        String linePrice = currentItem.getLinePrice();

        TextView tvQuantity = lvEstimate.findViewById(R.id.estimate_item_quantity);
        TextView tvMaterial = lvEstimate.findViewById(R.id.estimate_item_material);
        TextView tvDescription = lvEstimate.findViewById(R.id.estimate_item_description);
        TextView tvProductName = lvEstimate.findViewById(R.id.estimate_product_name);
        TextView tvUnitPrice = lvEstimate.findViewById(R.id.estimate_item_unit_price);
        TextView tvLineItemPrice = lvEstimate.findViewById(R.id.estimate_item_line_price);
        // Populate fields with extracted properties
        tvQuantity.setText(quantity);
        tvMaterial.setText(material);
        tvProductName.setText(productName);
        tvDescription.setText(description);
        if (unitPrice.equals("") ){
            tvUnitPrice.setText(" ");
        } else {
            tvUnitPrice.setText(String.format(Locale.CANADA, format, Double.valueOf(unitPrice)));
        }
        try {
            tvLineItemPrice.setText(String.format(Locale.CANADA, format, Double.valueOf(linePrice)));
        } catch (NumberFormatException e) {
            tvLineItemPrice.setText("");
        }
        return lvEstimate;
    }
}
