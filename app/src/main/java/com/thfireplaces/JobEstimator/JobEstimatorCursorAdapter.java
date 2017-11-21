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
 * {@link JobEstimatorCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product type data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
class JobEstimatorCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link JobEstimatorCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    JobEstimatorCursorAdapter(Context context, Cursor c) {
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
        return LayoutInflater.from(context).inflate(R.layout.list_item_job, parent, false);
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
        TextView tvInstallDate = view.findViewById(R.id.install_date);
        TextView tvjobName = view.findViewById(R.id.job_name);
        TextView tvJobNumber = view.findViewById(R.id.job_number);
        TextView tvJobAddress = view.findViewById(R.id.job_address);
        TextView tvJobLocation = view.findViewById(R.id.job_location);
        TextView tvJobPostal = view.findViewById(R.id.job_postal);
        // Extract properties from cursor
        String installDate = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_INSTALL_DATE));
        String jobName = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_NAME));
        String jobNumber = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_JOB_NUMBER));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_ADDRESS));
        String city = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_CITY));
        String province = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_PROVINCE));
        String postal = cursor.getString(cursor.getColumnIndexOrThrow(JobEstimatorContract.JobTable.COL_POST_CODE));
        String location = city + ", " + province;
        // Populate fields with extracted properties
        tvjobName.setText(jobName);
        tvJobNumber.setText(jobNumber);
        tvInstallDate.setText(installDate);
        tvJobAddress.setText(address);
        tvJobLocation.setText(location);
        tvJobPostal.setText(postal);
        int pos = cursor.getPosition();

        if (pos % 2 == 0) {
            view.setBackgroundColor(0xFFE3F2FD);
        } else {
            view.setBackgroundColor(0xFFBBDEFB);
        }
    }
}
