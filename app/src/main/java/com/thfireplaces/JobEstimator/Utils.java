package com.thfireplaces.JobEstimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thfireplaces.JobEstimator.data.JobEstimatorContract;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility functions
 */

final public class Utils {
    public Utils() {
    }


    public static ArrayList<String> decodeEntries(String input, char delimiter) {
        ArrayList<String> value = new ArrayList<>();

        if (!TextUtils.isEmpty(input)) {
            int start = 0;
            int idx;
            idx = input.indexOf(delimiter, start);
            while (idx != -1) {
                String sub = input.substring(start, idx);
                value.add(sub);
                start = idx + 1;
                idx = input.indexOf(delimiter, start);
            }
        }
        return value;
    }

    public static String getCurrentDate(boolean includeTime) {
        //Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = !includeTime ? new SimpleDateFormat("yyyy-MM-dd ", Locale.CANADA) : new SimpleDateFormat("yyyyMMddHHmmss", Locale.CANADA);

        return mdformat.format(Calendar.getInstance().getTime());

    }

    public static ArrayList<String> getJobSelections(Context context, Uri uri) {
        Cursor jobCursor;
        ArrayList<String> materialCodes = new ArrayList<>();
        String[] projection = {
                JobEstimatorContract.JobTable._ID,
                JobTable.COL_PRODUCT_SELECTIONS
        };
        jobCursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
        if (jobCursor != null) {
            if (jobCursor.moveToFirst()) {
                String mSelections = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PRODUCT_SELECTIONS));
                if (!TextUtils.isEmpty(mSelections)) mSelections = mSelections.trim();

                materialCodes.clear();
                if (!TextUtils.isEmpty(mSelections)) {
                    materialCodes = decodeEntries(mSelections, ':');
                }
            }
            jobCursor.close();
        }
        return materialCodes;
    }

    public static String getJobPictures(Context context, Uri uri) {
        String pictures = "";
        String[] projection = {
                JobEstimatorContract.JobTable._ID,
                JobTable.COL_PICTURES
        };
        Cursor jobCursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
        if (jobCursor != null) {
            if (jobCursor.moveToFirst()) {
                pictures = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PICTURES));
                if (!TextUtils.isEmpty(pictures))
                    pictures = pictures.trim();
                else
                    pictures = " ";
            }
            jobCursor.close();
        }
        return pictures;
    }

    public static String getJobEmail(Context context, Uri uri) {
        String custEmailAddress = "";
        String[] projection = {
                JobTable._ID,
                JobTable.COL_EMAIL
        };
        Cursor jobCursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
        if (jobCursor != null) {
            if (jobCursor.moveToFirst()) {
                custEmailAddress = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_EMAIL));
                if (!TextUtils.isEmpty(custEmailAddress))
                    custEmailAddress = custEmailAddress.trim();
                else
                    custEmailAddress = " ";
            }
            jobCursor.close();
        }
        return custEmailAddress;
    }

    public static void updateJobWithContent(Context ctx, ContentValues values, Uri jobUri) {

        int nRows = ctx.getContentResolver().update(jobUri,
                values,
                null,
                null);
        if (nRows == 0) { // no rows - empty jobnumber table.  make the first entry
            Log.v("Utils", "Failed to update job_table with new values" + values.toString());
        }
    }

    public static void setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 300 * (listView.getResources().getDisplayMetrics().density);
                item.measure(
                        View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

/*
    public static Boolean matchFileWildcard(String fileName, String pattern) {
        File f = new File(fileName);
        if (f.exists() && f.isDirectory()) {
            final Pattern p = Pattern.compile(pattern);

            File[] flists = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return p.matcher(file.getName()).matches();
                }
            });

        }
        return false;

    }
*/

}
