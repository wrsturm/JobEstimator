package com.thfireplaces.JobEstimator;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobNumberTable;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.util.Log.v;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.widget.CompoundButton.OnTouchListener;

public class EditJob extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditJob.class.getSimpleName();
    private static final int JOBESTIMATOR_LOADER = 0;
    private String mDiscount;
    private boolean mjobHasChanged = false;
    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mjobHasChanged = true;
            return view.isEnabled() && view.isClickable() && view.performClick();
        }

    };
    private final OnCheckedChangeListener checkBoxListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mjobHasChanged = true;
            if (isChecked)
                buttonView.setChecked(true);
            else
                buttonView.setChecked(false);
        }
    };

    private Cursor cursor;
    private int mConstructionType;
    private Uri mCurrentJobUri;
    //Variables to hold the inputs
    private EditText etInsDate;
    private EditText etCusName;
    private EditText etAddress;
    private EditText etCity;
    private EditText etProvince;
    private EditText etDiscount;
    private EditText etPostalCode;
    private EditText etResPhone;
    private EditText etBusPhone;
    private EditText etCellPhone;
    private EditText etEmail;
    private EditText etJobInstructions;
    private TextView tvProductSelection;
    private TextView tvPictures;
    private String mInsDate;
    private String mCusName;
    private String mAddress;
    private String mCity;
    private String mProvince;
    private String mPostalCode;
    private String mResPhone;
    private String mBusPhone;
    private String mCellPhone;
    private String mEmail;
    private String mJobInstructions;
    private String mSelections = "";
    private String mPictures;
    private EditText etJobNum;
    private EditText etDate;
    private String mPrefixJobNum;
    private String mJobNum;
    private String mCurDate;
    private CheckBox cbTwoStorey;
    private CheckBox cbSingleStorey;
    private CheckBox cbMainFloor;
    private CheckBox cbCrossCorner;
    private CheckBox cbBasement;
    private CheckBox cbInsideWall;
    private CheckBox cbOutsideWall;
    private CheckBox cbThroughWood;
    private CheckBox cbThroughConcrete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset the screen back to regular theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ids of all the Customer Information fields

        etInsDate = findViewById(R.id.installation_date_edit_text);
        etJobNum = findViewById(R.id.job_number_edit_text);
        etCusName = findViewById(R.id.customer_name_edit_text);
        etDate = findViewById(R.id.date_edit_text);
        etAddress = findViewById(R.id.address_edit_text);
        etCity = findViewById(R.id.city_edit_text);
        etProvince = findViewById(R.id.province_edit_text);
        etPostalCode = findViewById(R.id.postal_code_edit_text);
        etResPhone = findViewById(R.id.res_phone_number_edit_text);
        etBusPhone = findViewById(R.id.business_phone_number_edit_text);
        etCellPhone = findViewById(R.id.cell_phone_number_edit_text);
        etEmail = findViewById(R.id.email_edit_text);
        etJobInstructions = findViewById(R.id.job_instructions_edit_text);
        etDiscount = findViewById(R.id.discount_edit_text);
        tvProductSelection = findViewById(R.id.products_selected);
        tvPictures = findViewById(R.id.job_pictures);

        cbTwoStorey = findViewById(R.id.two_storey_checkbox);
        cbSingleStorey = findViewById(R.id.single_storey_checkbox);
        cbMainFloor = findViewById(R.id.main_floor_checkbox);
        cbInsideWall = findViewById(R.id.inside_wall_checkbox);
        cbOutsideWall = findViewById(R.id.outside_wall_checkbox);
        cbCrossCorner = findViewById(R.id.cross_corner_checkbox);
        cbBasement = findViewById(R.id.basement_checkbox);
        cbThroughWood = findViewById(R.id.through_wood_checkbox);
        cbThroughConcrete = findViewById(R.id.through_concrete_checkbox);


        // Set up a touch listener on all the fields
        // just touch will mark it as changed
        etInsDate.setOnTouchListener(mTouchListener);

        etCusName.setOnTouchListener(mTouchListener);
        etAddress.setOnTouchListener(mTouchListener);
        etCity.setOnTouchListener(mTouchListener);
        etProvince.setOnTouchListener(mTouchListener);
        etPostalCode.setOnTouchListener(mTouchListener);
        etResPhone.setOnTouchListener(mTouchListener);
        etBusPhone.setOnTouchListener(mTouchListener);
        etCellPhone.setOnTouchListener(mTouchListener);
        etEmail.setOnTouchListener(mTouchListener);
        etJobInstructions.setOnTouchListener(mTouchListener);
        etDiscount.setOnTouchListener(mTouchListener);


        cbSingleStorey.setOnCheckedChangeListener(checkBoxListener);
        cbTwoStorey.setOnCheckedChangeListener(checkBoxListener);
        cbMainFloor.setOnCheckedChangeListener(checkBoxListener);
        cbBasement.setOnCheckedChangeListener(checkBoxListener);
        cbInsideWall.setOnCheckedChangeListener(checkBoxListener);
        cbOutsideWall.setOnCheckedChangeListener(checkBoxListener);
        cbThroughWood.setOnCheckedChangeListener(checkBoxListener);
        cbThroughConcrete.setOnCheckedChangeListener(checkBoxListener);
        cbCrossCorner.setOnCheckedChangeListener(checkBoxListener);


        mCurrentJobUri = getIntent().getData();
        Log.v(LOG_TAG, "URI: " + mCurrentJobUri);

        mJobNum = getNextJobNum();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mCurDate = Utils.getCurrentDate(false);
        mPrefixJobNum = sharedPref.getString("username", "JobEstimator").trim() + "-" + mJobNum;
        etJobNum.setText(mPrefixJobNum);
        etDate.setText(mCurDate);

        if (mCurrentJobUri == null) { //Adding a new job
            setTitle(getString(R.string.customer_add_info_title));
        } else { //Editing an existing job
            setTitle(getString(R.string.customer_edit_info_title));
            getLoaderManager().initLoader(JOBESTIMATOR_LOADER, null, this);
        }
    }

    private void saveJob() {
        // Get the input fields
        mInsDate = etInsDate.getText().toString().trim();
        mPrefixJobNum = etJobNum.getText().toString().trim();
        mCusName = etCusName.getText().toString().trim();
        mCurDate = etDate.getText().toString().trim();
        mAddress = etAddress.getText().toString().trim();
        mCity = etCity.getText().toString().trim();
        mProvince = etProvince.getText().toString().trim();
        mPostalCode = etPostalCode.getText().toString().trim();
        mResPhone = etResPhone.getText().toString().trim();
        mBusPhone = etBusPhone.getText().toString().trim();
        mCellPhone = etCellPhone.getText().toString().trim();
        mEmail = etEmail.getText().toString().trim();
        mJobInstructions = etJobInstructions.getText().toString().trim();
        mDiscount = etDiscount.getText().toString().trim();

        if (mCurrentJobUri == null &&
                TextUtils.isEmpty(mInsDate) &&
                TextUtils.isEmpty(mCusName) &&
                TextUtils.isEmpty(mAddress) &&
                TextUtils.isEmpty(mCity) &&
                TextUtils.isEmpty(mProvince) &&
                TextUtils.isEmpty(mPostalCode) &&
                TextUtils.isEmpty(mResPhone) &&
                TextUtils.isEmpty(mBusPhone) &&
                TextUtils.isEmpty(mCellPhone) &&
                TextUtils.isEmpty(mEmail) &&
                TextUtils.isEmpty(mDiscount) &&
                TextUtils.isEmpty(mJobInstructions)
                ) {
            finish();
            return;
        }
        if (!mjobHasChanged) {
            finish();
            return;
        }


// Set a flag on multiple checks when not allowed
        boolean mConflicting = false;
        mConstructionType = 0;

        if (cbCrossCorner.isChecked()) mConstructionType |= JobTable.CONSTRUCTION_TYPE_CROSS_CORNER;

        if (cbBasement.isChecked() && cbMainFloor.isChecked()) {
            Toast.makeText(this, R.string.conflict_floor, Toast.LENGTH_LONG).show();
            mConflicting = true;
        } else {
            if (cbMainFloor.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_MAIN_FLOOR;
            }
            if (cbBasement.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_BASEMENT;
            }
        }

        if (cbTwoStorey.isChecked() && cbSingleStorey.isChecked()) {
            Toast.makeText(this, R.string.conflict_storey, Toast.LENGTH_LONG).show();
            mConflicting = true;
        } else {
            if (cbSingleStorey.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_SINGLE_STOREY;
            }
            if (cbTwoStorey.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_TWO_STOREY;
            }
        }

        if (cbThroughConcrete.isChecked() && cbThroughWood.isChecked()) {
            Toast.makeText(this, R.string.conflict_through, Toast.LENGTH_LONG).show();
            mConflicting = true;
        } else {
            if (cbThroughWood.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_THROUGH_WOOD;
            }
            if (cbThroughConcrete.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_THROUGH_CONCRETE;
            }
        }

        if (cbInsideWall.isChecked() && cbOutsideWall.isChecked()) {
            Toast.makeText(this, R.string.conflict_wall, Toast.LENGTH_LONG).show();
            mConflicting = true;
        } else {
            if (cbInsideWall.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_INSIDE_WALL;
            }
            if (cbOutsideWall.isChecked()) {
                mConstructionType |= JobTable.CONSTRUCTION_TYPE_OUTSIDE_WALL;
            }
        }

        if (mConflicting)
            return;

        ContentValues cusValues = new ContentValues();
        cusValues.put(JobTable.COL_INSTALL_DATE, mInsDate);
        cusValues.put(JobTable.COL_JOB_NUMBER, mPrefixJobNum);
        cusValues.put(JobTable.COL_NAME, mCusName);
        cusValues.put(JobTable.COL_DATE, mCurDate);
        cusValues.put(JobTable.COL_ADDRESS, mAddress);
        cusValues.put(JobTable.COL_CITY, mCity);
        cusValues.put(JobTable.COL_PROVINCE, mProvince);
        cusValues.put(JobTable.COL_POST_CODE, mPostalCode);
        cusValues.put(JobTable.COL_PHONE_RES, mResPhone);
        cusValues.put(JobTable.COL_PHONE_BUS, mBusPhone);
        cusValues.put(JobTable.COL_PHONE_MOB, mCellPhone);
        cusValues.put(JobTable.COL_EMAIL, mEmail);
        cusValues.put(JobTable.COL_CONSTRUCTION_TYPE, mConstructionType);
        cusValues.put(JobTable.COL_DISCOUNT, mDiscount);
        cusValues.put(JobTable.COL_JOB_INSTRUCTIONS, mJobInstructions);
        cusValues.put(JobTable.COL_PRODUCT_SELECTIONS, mSelections);
        cusValues.put(JobTable.COL_PICTURES, mPictures);

        if (mCurrentJobUri == null) { // New job entry
            Uri newUri = getContentResolver().insert(JobTable.CONTENT_URI, cusValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_cus_failed), Toast.LENGTH_SHORT).show();
            } else {
                updateJobNum(mJobNum);
                Toast.makeText(this, getString(R.string.insert_cus_successful), Toast.LENGTH_SHORT).show();
                mjobHasChanged = false;
                mCurrentJobUri = newUri;
            }
        } else { //Editing an entry via onClickItem path
            int rowCnt = getContentResolver().update(mCurrentJobUri, cusValues, null, null);
            if (rowCnt == 0) {
                Toast.makeText(this, getString(R.string.update_cus_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_cus_successful), Toast.LENGTH_SHORT).show();
                mjobHasChanged = false;
            }
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
        if (cursor.moveToFirst()) {
            mInsDate = cursor.getString(cursor.getColumnIndex(JobTable.COL_INSTALL_DATE));
            mPrefixJobNum = cursor.getString(cursor.getColumnIndex(JobTable.COL_JOB_NUMBER));
            mCurDate = cursor.getString(cursor.getColumnIndex(JobTable.COL_DATE));
            mCusName = cursor.getString(cursor.getColumnIndex(JobTable.COL_NAME));
            mAddress = cursor.getString(cursor.getColumnIndex(JobTable.COL_ADDRESS));
            mCity = cursor.getString(cursor.getColumnIndex(JobTable.COL_CITY));
            mProvince = cursor.getString(cursor.getColumnIndex(JobTable.COL_PROVINCE));
            mPostalCode = cursor.getString(cursor.getColumnIndex(JobTable.COL_POST_CODE));
            mResPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_RES));
            mBusPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_BUS));
            mCellPhone = cursor.getString(cursor.getColumnIndex(JobTable.COL_PHONE_MOB));
            mEmail = cursor.getString(cursor.getColumnIndex(JobTable.COL_EMAIL));
            mConstructionType = cursor.getInt(cursor.getColumnIndex(JobTable.COL_CONSTRUCTION_TYPE));
            mDiscount = cursor.getString(cursor.getColumnIndex(JobTable.COL_DISCOUNT));
            mJobInstructions = cursor.getString(cursor.getColumnIndex(JobTable.COL_JOB_INSTRUCTIONS));
            mSelections = cursor.getString(cursor.getColumnIndex(JobTable.COL_PRODUCT_SELECTIONS));
            mPictures = cursor.getString(cursor.getColumnIndex(JobTable.COL_PICTURES));
            etInsDate.setText(mInsDate);
            etCusName.setText(mCusName);
            etJobNum.setText(mPrefixJobNum);
            etDate.setText(mCurDate);
            etAddress.setText(mAddress);
            etCity.setText(mCity);
            etProvince.setText(mProvince);
            etPostalCode.setText(mPostalCode);
            etResPhone.setText(mResPhone);
            etBusPhone.setText(mBusPhone);
            etCellPhone.setText(mCellPhone);
            etEmail.setText(mEmail);
            etDiscount.setText(mDiscount);
            etJobInstructions.setText(mJobInstructions);
            tvProductSelection.setText(mSelections);
            tvPictures.setText(mPictures);

            // The following trigger the OnCheckedChangeListener so reset mJobHasChanged
            cbSingleStorey.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_SINGLE_STOREY));
            cbTwoStorey.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_TWO_STOREY));
            cbMainFloor.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_MAIN_FLOOR));
            cbBasement.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_BASEMENT));
            cbInsideWall.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_INSIDE_WALL));
            cbOutsideWall.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_OUTSIDE_WALL));
            cbThroughWood.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_THROUGH_WOOD));
            cbThroughConcrete.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_THROUGH_CONCRETE));
            cbCrossCorner.setChecked(set_checked(JobTable.CONSTRUCTION_TYPE_CROSS_CORNER));
            mjobHasChanged = false;
        }
    }

    private void deleteJob() {
        if (mCurrentJobUri != null) {
            File estimatesDir = getExternalFilesDir(DIRECTORY_DOCUMENTS);
            File[] jobfiles;
            final Pattern p = Pattern.compile(mPrefixJobNum + ".*");

            if (estimatesDir != null) {
                jobfiles = estimatesDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return p.matcher(file.getName()).matches();
                    }
                });

                for (File efd : jobfiles) {

                    if (efd.exists()) {
                        boolean efdDelOk = efd.delete();
                        if (efdDelOk)
                            Toast.makeText(this, "Deleted: " + efd.getName(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(this, "Failed to delete: " + efd.getName(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            if (mPictures != null && !mPictures.isEmpty()) {
                // Pictures exist
                File[] pic;
                File picturesDir = getExternalFilesDir(DIRECTORY_PICTURES);
                if (picturesDir != null) {
                    pic = picturesDir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return p.matcher(file.getName()).matches();
                        }
                    });


                    for (File efd : pic) {

                        if (efd.exists()) {
                            boolean efdDelOk = efd.delete();
                            if (efdDelOk)
                                Toast.makeText(this, "Deleted: " + efd.getName(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(this, "Failed to delete: " + efd.getName(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            int rowCnt = getContentResolver().delete(mCurrentJobUri, null, null);
            if (rowCnt == 0) {
                Toast.makeText(this, getString(R.string.update_delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_delete_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private boolean set_checked(int bit) {
        return ((mConstructionType & bit) != 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        resetInputFields();
        cursor.close();
        finish();
    }

    /* Query the job_number table for a job number
     * @returns the next job number as a string
     */
    private String getNextJobNum() {
        int job = 0;
        String jobNum;

        cursor = getContentResolver().query(JobNumberTable.CONTENT_URI,
                JobNumberTable.jobNumProjection,
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String result = cursor.getString(cursor.getColumnIndex(JobTable.COL_JOB_NUMBER));
                job = Integer.parseInt(result) + 1;
            } else { //first time job number has been requested
                job = 1;
            }
            cursor.close();
        }
        jobNum = Integer.toString(job);
        return jobNum;
    }

    private void updateJobNum(String jobnum) {
        ContentValues values = new ContentValues();
        values.put(JobNumberTable.COL_JOB_NUMBER, jobnum);
        values.put(JobNumberTable.COL_DATE, Utils.getCurrentDate(false));

        int nRows = getContentResolver().update(JobNumberTable.CONTENT_URI,
                values,
                null,
                null);
        if (nRows == 0) { // no rows - empty jobnumber table.  make the first entry
            v(LOG_TAG, "Failed to update job_number table with job number " + jobnum);
        }
    }

/*
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ", Locale.CANADA);
        return mdformat.format(calendar.getTime());
    }
*/

    private void resetInputFields() {
        etInsDate.setText("");
        etJobNum.setText("");
        etCusName.setText("");
        etDate.setText("");
        etAddress.setText("");
        etCity.setText("");
        etProvince.setText("");
        etPostalCode.setText("");
        etResPhone.setText("");
        etBusPhone.setText("");
        etCellPhone.setText("");
        etEmail.setText("");
        etDiscount.setText("");
        etJobInstructions.setText("");
        tvProductSelection.setText("");
        tvPictures.setText("");

        cbSingleStorey.setChecked(false);
        cbTwoStorey.setChecked(false);
        cbMainFloor.setChecked(false);
        cbBasement.setChecked(false);
        cbInsideWall.setChecked(false);
        cbOutsideWall.setChecked(false);
        cbThroughWood.setChecked(false);
        cbThroughConcrete.setChecked(false);
        cbCrossCorner.setChecked(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editjob, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save:
                saveJob();
                return true;
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_select:
                if (mCurrentJobUri != null) {
                    if (mjobHasChanged) {
                        saveJob();
                    }
                    Intent intent = new Intent(EditJob.this, Supplier.class);
                    Log.v(LOG_TAG, mCurrentJobUri.toString());
                    intent.setData(mCurrentJobUri);
                    intent.putExtra("selections", mSelections);
                    startActivity(intent);
                }
                return true;
            case R.id.action_delete_job:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_view_estimate:
                invokeViewEstimate();
                return true;
            case R.id.action_take_picture:
                if (mCurrentJobUri == null) {
                    Toast.makeText(EditJob.this, getString(R.string.save_data_first),
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                Intent intent = new Intent(EditJob.this, TakePicture.class);
                intent.setData(mCurrentJobUri);
                intent.putExtra("jobprefix", mPrefixJobNum);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_job);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the job.
                deleteJob();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the job.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void invokeViewEstimate() {
        if (mCurrentJobUri != null) {
            if (mjobHasChanged) {
                saveJob();
/*
                Toast.makeText(EditJob.this, getString(R.string.save_data_first),
                        Toast.LENGTH_LONG).show();
                return;
*/
            }
            Intent intent = new Intent(EditJob.this, ViewEstimate.class);
            v(LOG_TAG, mCurrentJobUri.toString());
            intent.setData(mCurrentJobUri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Job is not valid for action", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
