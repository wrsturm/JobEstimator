package com.thfireplaces.JobEstimator;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract.AccessoriesTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.ProductTable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.BitmapFactory.decodeFile;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_PICTURES;

public class ViewEstimate extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ViewEstimate.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    private static String EMAIL_CENTRAL;
    private static String USERNAME;
    private static double TAX_RATE;
    private static double PERMIT_PRICE;
    private static double INSTALLATION_UNIT_PRICE;
    private static String INSTALLATION_DEFAULT_UNITS;
    TextView tvQuantity;
    TextView tvLinePrice;
    String mDiscount;
    boolean estimateNeedsSaving = true;
    private double discount;
    private boolean showDiscount = false;
    private boolean showDiscountWas = showDiscount;
    private Uri mCurrentJobUri;
    private int mConstructionType;
    private Cursor jobCursor = null;
    private ArrayList<Item> items;
    private double totalPrice = 0, tax = 0, subtotal = 0;
    private PdfDocument document;
    private String mPrefixJobNum;
    private String newQuantity;
    private EditText input;
    private TextView tvMaterial;
    private TextView tvUnitPrice;
    private ViewEstimateItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.view_estimate_title));
        setContentView(R.layout.activity_view_estimate);
        items = new ArrayList<>();
        //fillCustomerInfo();
        mCurrentJobUri = getIntent().getData();
        //readSharedPref();

        ListView lvEstimate = findViewById(R.id.estimate_listview);
        itemAdapter = new ViewEstimateItemAdapter(this, items);
        lvEstimate.setAdapter(itemAdapter);
        lvEstimate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvMaterial = view.findViewById(R.id.estimate_item_material);
                tvQuantity = view.findViewById(R.id.estimate_item_quantity);
                tvUnitPrice = view.findViewById(R.id.estimate_item_unit_price);
                tvLinePrice = view.findViewById(R.id.estimate_item_line_price);

                changeQuantityAlert(position);
            }
        });
        getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);
    }

    /**
     * Get the values for settings
     * TAX_RATE
     * PERMIT_PRICE
     * INSTALLATION_UNIT_PRICE
     * INSTALLATION_DEFAULT_UNITS
     * EMAIL_CENTRAL
     * USERNAME
     */
    private void readSharedPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        TAX_RATE = Double.valueOf(sharedPref.getString(String.valueOf(R.string.settings_tax_key), getString(R.string.settings_tax_default)));
        PERMIT_PRICE = Double.valueOf(sharedPref.getString(String.valueOf(R.string.settings_permit_key), getString(R.string.settings_permit_default)));
        INSTALLATION_UNIT_PRICE = Double.valueOf(sharedPref.getString(String.valueOf(R.string.settings_installation_price_key), getString(R.string.settings_installation_price_default)));
        INSTALLATION_DEFAULT_UNITS = sharedPref.getString(String.valueOf(R.string.settings_installation_units_key), getString(R.string.settings_installation_units_default));
        EMAIL_CENTRAL = sharedPref.getString(String.valueOf(R.string.settings_central_email_key), getString(R.string.settings_central_email_default));
        USERNAME = sharedPref.getString(String.valueOf(R.string.settings_user_name_key), getString(R.string.settings_user_name_default));
    }

    /**
     * @param uri Current job URI
     */
    private void emailEstimate(Uri uri) {
        String email = Utils.getJobEmail(this, uri).trim();
        String emailMessage = "The following is the estimate for job number " + mPrefixJobNum +
                ".  Please take a moment to review and inform us of any discrepancies.";
        Log.v("emailEstimate: ", USERNAME + " " + EMAIL_CENTRAL + " " + email);

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // If there is no customer email then set the Ro: address to the central email
            if (!email.isEmpty()) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{EMAIL_CENTRAL});
            } else {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_CENTRAL});
            }
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Estimate for job: " + mPrefixJobNum);
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);
            File jobFile = new File(getExternalFilesDir(DIRECTORY_DOCUMENTS), mPrefixJobNum + ".pdf");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(jobFile));

            startActivity(emailIntent);
            Toast.makeText(this, "EMail..." + email + "," + EMAIL_CENTRAL, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(LOG_TAG, "emailEstimate() failed to start activity.", e);
            Toast.makeText(this, "No handler", Toast.LENGTH_LONG).show();
        }

    }


    private void changeQuantity(String newValue, int position, long id) {
        if (TextUtils.isEmpty(newValue)) return;
        String material = tvMaterial.getText().toString();
        String up = tvUnitPrice.getText().toString();
        double unitPrice;
        double newQuantity;
        Item item;

        try {
            newQuantity = Double.valueOf(newValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error in value" + newValue, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            unitPrice = Double.valueOf(up);
        } catch (NumberFormatException e) {
            unitPrice = 0.0;
        }
        double newLinePrice = newQuantity * unitPrice;

        Log.v(LOG_TAG, "changeQuantity: " + material + ":" + up + ":" + position + ":" + id);
        item = items.get(position);
        item.setQuantity(newValue);
        item.setLinePrice(String.format(Locale.CANADA, "%8.2f", newLinePrice));
        //Log.v(LOG_TAG, item.);
        items.set(position, item);
        recalculateTotal();
        itemAdapter.notifyDataSetChanged();
        estimateNeedsSaving = true;
    }

    private void recalculateTotal() {
        String format = "%8.2f";
        String quantity = "", material = "", name, description, price = "", linePrice;

        int summaryLines, sidxOffset;
        Item item;

        subtotal = 0;
        tax = 0;
        totalPrice = 0;
        if (!showDiscountWas) {
            summaryLines = 5;
            sidxOffset = 0;
        } else {
            summaryLines = 6;
            showDiscountWas = showDiscount;
            sidxOffset = 1;
        }
        int size = items.size();
        int summaryIndex = size - 1;
        int removeLines = summaryIndex - summaryLines;

        for (int idx = 0; idx <= removeLines; idx++) {
            item = items.get(idx);
            subtotal += Double.valueOf(item.getLinePrice());
        }
// Strip back through Discount summary line
        // Last items:
        //      Discount
        //      Permits
        //      Installation
        //      Subtotal
        //      GSTquantity
        //      Total
        ArrayList<Item> saveItems = new ArrayList<>(items.subList(removeLines + 1, summaryIndex));

        for (int idx = summaryIndex; idx > removeLines; idx--) {
            items.remove(idx);

        }
        //Re-add in the new values
        if (showDiscount) {
            double discountValue = discount * subtotal;
            String discountDesc = "%s %% discount applied";
            linePrice = String.format("%s", String.format(Locale.CANADA, format, -discountValue));
            subtotal = subtotal - discountValue;
            name = getString(R.string.action_do_discount);

            items.add(new Item(quantity, material, name, String.format(Locale.CANADA, discountDesc, mDiscount), price, linePrice));
        }
        int sidx = sidxOffset; // Offset to PERMITS entry in saveItems offset 0 is Discount
        // PERMITS - NET value - no discount
        {
            quantity = saveItems.get(sidx).getQuantity().trim();
            if (!quantity.equals("0") && !quantity.isEmpty()) {
                name = "Permits";
                description = "Required building permits";
                linePrice = String.valueOf(PERMIT_PRICE * Double.valueOf(quantity));
                items.add(new Item(quantity, "", name, description, String.valueOf(PERMIT_PRICE), linePrice));
                subtotal += Double.valueOf(linePrice);
            } else
                // Blank the permits line but leave it in the same position in case it is to be added again
                items.add(new Item("", "", "", "", "", ""));

        }

        // INSTALLATION
        sidx += 1;
        {
            quantity = saveItems.get(sidx).getQuantity();
            String insDesc = "at $%s / unit";
            linePrice = String.valueOf(INSTALLATION_UNIT_PRICE * Double.valueOf(quantity));
            name = "Installation";
            items.add(new Item(quantity, "", name, String.format(Locale.CANADA, insDesc, INSTALLATION_UNIT_PRICE), String.valueOf(INSTALLATION_UNIT_PRICE), linePrice));
            subtotal += Double.valueOf(linePrice);
        }


        name = "Subtotal";
        linePrice = String.format(Locale.CANADA, format, subtotal);
        items.add(new Item("", "", name, "", "", linePrice));

        tax = TAX_RATE * subtotal;
        name = "GST";
        String taxDesc = "at %s%%";
        String taxDisp = Double.toString(TAX_RATE * 100);

        linePrice = String.format(Locale.CANADA, format, tax);
        items.add(new Item("", "", name, String.format(Locale.CANADA, taxDesc, taxDisp), "", linePrice));

        totalPrice = subtotal + tax;
        name = "Total Price";
        linePrice = String.format(Locale.CANADA, format, totalPrice);
        items.add(new Item("", "", name, "", "", linePrice));
    }

    private void changeQuantityAlert(int position) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEstimate.this);
        builder.setMessage("New quantity");
        builder.setTitle("Change Quantity");
        input = new EditText(this);
        //input.setBackgroundColor(0xFFE3F2FD);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        final int pos = position;

        builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Change" button.
                newQuantity = input.getText().toString();

                changeQuantity(newQuantity, pos, id);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_view_estimate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save_estimate:
                if (estimateNeedsSaving) {
                    save_estimate();
                }
                return true;

            case R.id.action_email_estimate:
                if (estimateNeedsSaving) save_estimate();
                emailEstimate(mCurrentJobUri);
                return true;

            case R.id.action_preferences:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                readSharedPref();
                recalculateTotal();
                itemAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Save the current view to a PDF file
    private void save_estimate() {
        //final int EXTRA_PADDING = 100;
        int pageNumber = 1;
        estimateNeedsSaving = false;
        //int size = items.size();
        //TODO get sizes of each section to calculate the actual space needed on a page and create extra pages if needed
        // section include page heading, customer info construction type, item list
        //View pdfHeader = findViewById(R.id.pdf_page_heading_section);
        //int pdfHeaderWidth = pdfHeader.getWidth();
        //int pdfHeaderHeight = pdfHeader.getHeight();
        //View cusInfo = findViewById(R.id.customer_info_job_id_header);
        //int cusInfoWidth = cusInfo.getWidth();
        //int cusInfoHeight = cusInfo.getHeight();

        ListView lvArea = findViewById(R.id.estimate_listview);
        Utils.setListViewHeightBasedOnItems(lvArea);
        ScrollView content = findViewById(R.id.activity_view_estimate);
        int width = content.getWidth();
        int height = content.getHeight();
// create a new document
        document = new PdfDocument();

        // create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageNumber).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        // draw on the page
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        String pictures = Utils.getJobPictures(this, mCurrentJobUri);
        if (!pictures.trim().isEmpty()) {

            ArrayList<String> pic;
            BitmapFactory.Options bmOptions;

            pic = Utils.decodeEntries(pictures, ':');
            bmOptions = new BitmapFactory.Options();

            for (int idx = 0; idx < pic.size(); idx++) {
                String fileName = getExternalFilesDir(DIRECTORY_PICTURES) + "/" + mPrefixJobNum + "_" + pic.get(idx) + ".jpg";
                File pFIle = new File(fileName);
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = 2;


                Bitmap bitmap = decodeFile(pFIle.getAbsolutePath(), bmOptions);

                if (bitmap != null) {
                    pageNumber += 1;
                    pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageNumber).create();
                    page = document.startPage(pageInfo);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    byte[] byteArray = stream.toByteArray();
                    Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    if (compressedBitmap != null) {
                        page.getCanvas().drawBitmap(compressedBitmap, 0, 0, null);
                        compressedBitmap.recycle();
                    }
                    document.finishPage(page);
                    bitmap.recycle();
                }
            }
        }
        outputToFile(mPrefixJobNum);
        // close the document
        document.close();
        //finish();
    }

    private void outputToFile(String fileName) {
        String renameFile = fileName + "-" + Utils.getCurrentDate(true);
        File estimatesDir = getExternalFilesDir(DIRECTORY_DOCUMENTS);
        File newFile = null;
        File oldFile = null;
        if (estimatesDir != null) {
            newFile = new File(estimatesDir.getAbsoluteFile() + "/" + fileName + ".pdf");
            oldFile = new File(estimatesDir + "/" + renameFile + ".pdf");
        }
        if (newFile != null && newFile.exists()) {
            final boolean renamed = newFile.renameTo(oldFile);
            if (renamed) {
                Toast.makeText(this, newFile.toString() + " renamed as " + oldFile.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, newFile.toString() + " failed to rename to " + oldFile.toString(), Toast.LENGTH_LONG).show();

            }
        }
        try {
            final boolean newFile1;
            newFile1 = (newFile != null) && newFile.createNewFile();
            try {
                if (newFile1) {
                    FileOutputStream pdfFile = new FileOutputStream(newFile);
                    document.writeTo(pdfFile);
                }
            } catch (FileNotFoundException e) {
                // ...
            }

        } catch (IOException e) {
            // ...
        }
        if (newFile != null) {
            Toast.makeText(this, "Saved as " + newFile.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = JobTable._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentJobUri))};
        return new CursorLoader(this,
                JobTable.CONTENT_URI,
                JobTable.jobProjection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        jobCursor = cursor;
        if (cursor.moveToFirst()) {
            String mInsDate = cursor.getString(cursor.getColumnIndex(JobTable.COL_INSTALL_DATE));
            mPrefixJobNum = cursor.getString(cursor.getColumnIndex(JobTable.COL_JOB_NUMBER));
            String mCurDate = cursor.getString(cursor.getColumnIndex(JobTable.COL_DATE));
            String mCusName = cursor.getString(cursor.getColumnIndex(JobTable.COL_NAME));
            String mAddress = cursor.getString(cursor.getColumnIndex(JobTable.COL_ADDRESS));
            String mCity = cursor.getString(cursor.getColumnIndex(JobTable.COL_CITY));
            String mProvince = cursor.getString(cursor.getColumnIndex(JobTable.COL_PROVINCE));
            String mPostalCode = cursor.getString(cursor.getColumnIndex(JobTable.COL_POST_CODE));
            String mResPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_RES));
            String mBusPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_BUS));
            String mCellPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_MOB));
            String mEmail = cursor.getString(cursor.getColumnIndex(JobTable.COL_EMAIL));
            mConstructionType = cursor.getInt(cursor.getColumnIndex(JobTable.COL_CONSTRUCTION_TYPE));
            mDiscount = cursor.getString(cursor.getColumnIndex(JobTable.COL_DISCOUNT));
            String mJobInstructions = cursor.getString(cursor.getColumnIndex(JobTable.COL_JOB_INSTRUCTIONS));

            // This needs to access the products table to fill in the details from the material codes
            // keep a running tally of the price
            String mSelections = cursor.getString(cursor.getColumnIndex(JobTable.COL_PRODUCT_SELECTIONS));

            TextView tvCustInsDate = findViewById(R.id.estimate_install_date);
            TextView tvCustJobNum = findViewById(R.id.estimate_job_id);
            TextView tvCustCusName = findViewById(R.id.estimate_customer_name);
            TextView tvCustDate = findViewById(R.id.estimate_date);
            TextView tvCustAddress = findViewById(R.id.estimate_customer_address);
            TextView tvCustCity = findViewById(R.id.estimate_customer_city);
            TextView tvCustProvince = findViewById(R.id.estimate_customer_province);
            TextView tvCustPostalCode = findViewById(R.id.estimate_customer_postal_code);
            TextView tvCustResPhone = findViewById(R.id.estimate_customer_home_phone);
            TextView tvCustBusPhone = findViewById(R.id.estimate_customer_bus_phone);
            TextView tvCustCellPhone = findViewById(R.id.estimate_customer_mobile);
            TextView tvCustEmail = findViewById(R.id.estimate_customer_email);
            TextView tvCustJobInstructions = findViewById(R.id.estimate_customer_job_instructions);

            CheckBox cbSingleStorey = findViewById(R.id.estimate_checkbox_single_storey);
            CheckBox cbTwoStorey = findViewById(R.id.estimate_checkbox_two_storey);
            CheckBox cbMainFloor = findViewById(R.id.estimate_checkbox_main_floor);
            CheckBox cbBasement = findViewById(R.id.estimate_checkbox_basement);
            CheckBox cbInsideWall = findViewById(R.id.estimate_checkbox_inside_wall);
            CheckBox cbOutsideWall = findViewById(R.id.estimate_checkbox_outside_wall);
            CheckBox cbThroughWood = findViewById(R.id.estimate_checkbox_through_wood);
            CheckBox cbThroughConcrete = findViewById(R.id.estimate_checkbox_through_concrete);
            CheckBox cbCrossCorner = findViewById(R.id.estimate_checkbox_cross_corner);

            tvCustInsDate.setText(mInsDate);
            tvCustJobNum.setText(mPrefixJobNum);
            tvCustCusName.setText(mCusName);
            tvCustDate.setText(mCurDate);
            tvCustAddress.setText(mAddress);
            tvCustCity.setText(mCity);
            tvCustProvince.setText(mProvince);
            tvCustPostalCode.setText(mPostalCode);
            tvCustResPhone.setText(mResPhone);
            tvCustBusPhone.setText(mBusPhone);
            tvCustCellPhone.setText(mCellPhone);
            tvCustEmail.setText(mEmail);
            tvCustJobInstructions.setText(mJobInstructions);

            cbSingleStorey.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_SINGLE_STOREY));
            cbTwoStorey.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_TWO_STOREY));
            cbMainFloor.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_MAIN_FLOOR));
            cbBasement.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_BASEMENT));
            cbThroughWood.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_THROUGH_WOOD));
            cbThroughConcrete.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_THROUGH_CONCRETE));
            cbCrossCorner.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_CROSS_CORNER));
            cbInsideWall.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_INSIDE_WALL));
            cbOutsideWall.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_OUTSIDE_WALL));

            if (!TextUtils.isEmpty(mSelections)) {
                String material = "";
                String description = "";
                String price = "0.00";
                String name = "";
                String quant = "1";

                String format = "%8.2f";
                int start = 0;
                int idx;
                String sub;
                String productSelection = ProductTable.COL_PRODUCT_MATERIAL + " =?";
                String accessoriesSelection = AccessoriesTable.COL_ACC_MATERIAL + " =?";
                double linePrice, unitPrice;
                double quantity;

                idx = mSelections.indexOf(":", start);
                while (idx != -1) {
                    sub = mSelections.substring(start, idx);
                    String selectArgs[] = new String[]{sub};

                    Cursor productCursor = getContentResolver().query(
                            ProductTable.CONTENT_URI,
                            ProductTable.productProjection,
                            productSelection,
                            selectArgs,
                            null
                    );
                    if (productCursor != null) {
                        if (productCursor.moveToFirst()) {
                            material = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_MATERIAL));
                            description = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_DESCRIPTION));
                            price = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_PRICE));
                            name = productCursor.getString(productCursor.getColumnIndexOrThrow(ProductTable.COL_PRODUCT_MODEL));
                        }
                        productCursor.close();
                    }

                    Cursor accessoriesCursor = getContentResolver().query(
                            AccessoriesTable.CONTENT_URI,
                            AccessoriesTable.accessoriesProjection,
                            accessoriesSelection,
                            selectArgs,
                            null
                    );

                    if (accessoriesCursor != null) {
                        if (accessoriesCursor.moveToFirst()) {
                            material = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_MATERIAL));
                            description = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_DESCRIPTION));
                            price = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_PRICE));
                            name = accessoriesCursor.getString(accessoriesCursor.getColumnIndexOrThrow(AccessoriesTable.COL_ACC_MODEL));
                        }
                        accessoriesCursor.close();
                    }

                    unitPrice = Double.valueOf(price);
                    quantity = Double.valueOf(quant);
                    linePrice = unitPrice * quantity;
                    subtotal = subtotal + linePrice;
                    items.add(new Item(quant, material, name, description, price, String.valueOf(linePrice)));
                    start = idx + 1;
                    idx = mSelections.indexOf(":", start);
                }
// start the summary of the estimate
                //
                if (TextUtils.isEmpty(mDiscount) || mDiscount.equals("")) {
                    showDiscount = false;
                    showDiscountWas = false;
                } else {
                    showDiscount = true;
                    showDiscountWas = true;
                    discount = Double.valueOf(mDiscount) / 100;
                }

                if (showDiscount) {
                    String discountDesc = String.format(Locale.CANADA, "%s %% discount applied", mDiscount);
                    double discountValue = discount * subtotal;
                    subtotal = subtotal - discountValue;
                    name = getString(R.string.action_do_discount);
                    items.add(new Item("", "", name, discountDesc, "", String.format(Locale.CANADA, format, -discountValue)));
                }
                readSharedPref();
                unitPrice = PERMIT_PRICE;
                quantity = Double.valueOf(quant);
                linePrice = unitPrice * quantity;
                material = "";
                name = "Permits";
                description = "Required building permits";
                price = String.valueOf(unitPrice);
                items.add(new Item(quant, material, name, description, price, String.valueOf(linePrice)));
                subtotal = subtotal + linePrice;

                unitPrice = INSTALLATION_UNIT_PRICE;
                quant = INSTALLATION_DEFAULT_UNITS;
                String insDesc = "at $%s / unit";
                quantity = Double.valueOf(quant);
                linePrice = unitPrice * quantity;
                material = "";
                name = "Installation";
                price = String.valueOf(unitPrice);
                items.add(new Item(quant, material, name, String.format(Locale.CANADA, insDesc, unitPrice), price, String.valueOf(linePrice)));
                subtotal = subtotal + linePrice;

                name = "Subtotal";
                items.add(new Item("", "", name, "", "", String.format(Locale.CANADA, format, subtotal)));

                String taxDesc = "at %s%%";
                String taxDisp = Double.toString(TAX_RATE * 100);
                tax = TAX_RATE * subtotal;
                name = "GST";
                items.add(new Item("", "", name, String.format(Locale.CANADA, taxDesc, taxDisp), "", String.format(Locale.CANADA, format, tax)));

                totalPrice = subtotal + tax;
                name = "Total Price";
                items.add(new Item("", "", name, "", "", String.format(Locale.CANADA, format, totalPrice)));
            }
            if (!items.isEmpty()) {
                ListView lvArea = findViewById(R.id.estimate_listview);
                Utils.setListViewHeightBasedOnItems(lvArea);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (jobCursor != null)
            jobCursor.close();
        finish();
    }

    private boolean set_checked(int bit) {
        return ((mConstructionType & bit) != 0);
    }
}
