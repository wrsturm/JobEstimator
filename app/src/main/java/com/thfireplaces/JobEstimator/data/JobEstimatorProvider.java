package com.thfireplaces.JobEstimator.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thfireplaces.JobEstimator.R;
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.ProductTypeTable;

public class JobEstimatorProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = JobEstimatorProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the rest of the tables
     */
    private static final int JOB_NUMBER = 1000;
    private static final int JOB_NUMBER_ID = 1001;

    private static final int JOB = 1010;
    private static final int JOB_ID = 1011;

    private static final int PRODUCT = 1020;
    private static final int PRODUCT_ID = 1021;

    private static final int ACCESSORIES = 1030;
    private static final int ACCESSORIES_ID = 1031;

    private static final int PRODUCT_TYPE = 1040;
    private static final int PRODUCT_TYPE_ID = 1041;

    private static final int CATEGORY = 1050;
    private static final int CATEGORY_ID = 1051;

    private static final int SUPPLIER = 1060;
    private static final int SUPPLIER_ID = 1061;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_JOB_NUMBER, JOB_NUMBER);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_JOB_NUMBER + "/#", JOB_NUMBER_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_JOB, JOB);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_JOB + "/#", JOB_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_PRODUCTS, PRODUCT);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_ACCESSORIES, ACCESSORIES);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_ACCESSORIES + "/#", ACCESSORIES_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_PRODUCT_TYPE, PRODUCT_TYPE);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_PRODUCT_TYPE + "/#", PRODUCT_TYPE_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_CATEGORY, CATEGORY);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_CATEGORY + "/#", CATEGORY_ID);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_SUPPLIER, SUPPLIER);
        sUriMatcher.addURI(JobEstimatorContract.CONTENT_AUTHORITY, JobEstimatorContract.PATH_SUPPLIER + "/#", SUPPLIER_ID);
    }

    private JobEstimatorDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Initalize an instance of the JobEstimatorDbHelper
        mDbHelper = new JobEstimatorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT_TYPE:
                // For the PRODUCT_TYPE code, query the product_type table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the product_type table.
                cursor = database.query(ProductTypeTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_TYPE_ID:
                // For the PRODUCT_TYPE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.thfireplaces/jobestimator/product_type/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductTypeTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the Product Type table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductTypeTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;


            case PRODUCT:
                cursor = database.query(JobEstimatorContract.ProductTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:
                selection = JobEstimatorContract.ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the product table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JobEstimatorContract.ProductTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case ACCESSORIES:
                cursor = database.query(JobEstimatorContract.AccessoriesTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case ACCESSORIES_ID:
                selection = JobEstimatorContract.AccessoriesTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the accessories table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JobEstimatorContract.AccessoriesTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case JOB:
                cursor = database.query(JobEstimatorContract.JobTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case JOB_ID:
                selection = JobEstimatorContract.JobTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the jod table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JobEstimatorContract.JobTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case CATEGORY:
                cursor = database.query(JobEstimatorContract.CategoryTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                Log.v(LOG_TAG, JobEstimatorContract.CategoryTable.TABLE_NAME + ": " + cursor.getCount());
                break;

            case CATEGORY_ID:
                selection = JobEstimatorContract.CategoryTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the category table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JobEstimatorContract.CategoryTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;

            case SUPPLIER:
                cursor = database.query(JobEstimatorContract.SupplierTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                Log.v(LOG_TAG, JobEstimatorContract.SupplierTable.TABLE_NAME + ": " + cursor.getCount());
                break;

            case SUPPLIER_ID:
                selection = JobEstimatorContract.SupplierTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the supplier table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JobEstimatorContract.SupplierTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;

            case JOB_NUMBER:
                cursor = database.query(JobEstimatorContract.JobNumberTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
// Get readable database
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case JOB:
                return insertJob(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a job into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertJob(Uri uri, ContentValues values) {
        sanityCheckInput(values);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(JobEstimatorContract.JobTable.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.job_insert_failed));
            return null;
        }
        // set up notifications if there ae changes
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // The job table is the only one that will get any inserts/ updates or deletions
            case JOB:
                sanityCheckInput(contentValues);
                return updateJob(uri, contentValues, selection, selectionArgs);
            case JOB_ID:
                // For the JOB_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                sanityCheckInput(contentValues);
                selection = JobEstimatorContract.JobTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateJob(uri, contentValues, selection, selectionArgs);


            // job_number table has one entry only, the job number and current date it was assigned
            case JOB_NUMBER:
                return updateJobNumber(uri, contentValues, null, null);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private void sanityCheckInput(ContentValues values) {
        // Check that the name is not null
        if (values.containsKey(JobEstimatorContract.JobTable.COL_NAME)) {
            String name = values.getAsString(JobEstimatorContract.JobTable.COL_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Job Estimator requires a customer name");
            }
        }
    }

    /**
     * Update job in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more jobs).
     * Return the number of rows that were successfully updated.
     */
    private int updateJob(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        sanityCheckInput(values);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int nRows = database.update(JobEstimatorContract.JobTable.TABLE_NAME, values, selection, selectionArgs);

        // set up notifications if there ae changes
        if (nRows != 0) getContext().getContentResolver().notifyChange(uri, null);

        return nRows;
    }

    private int updateJobNumber(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        sanityCheckInput(values);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(JobEstimatorContract.JobNumberTable.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int nRows;

        switch (match) {
            case JOB:
                // Delete all rows that match the selection and selection args
                // set up notifications if there ae changes
                nRows = database.delete(JobEstimatorContract.JobTable.TABLE_NAME, selection, selectionArgs);
                break;
            case JOB_ID:

                // Delete a single row given by the ID in the URI
                selection = JobEstimatorContract.JobTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                nRows = database.delete(JobEstimatorContract.JobTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Let all Listeners know if there has been any changes
        if (nRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return nRows;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return JobEstimatorContract.ProductTable.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return JobEstimatorContract.ProductTable.CONTENT_ITEM_TYPE;
            case PRODUCT_TYPE:
                return JobEstimatorContract.ProductTypeTable.CONTENT_LIST_TYPE;
            case PRODUCT_TYPE_ID:
                return JobEstimatorContract.ProductTypeTable.CONTENT_ITEM_TYPE;
            case ACCESSORIES:
                return JobEstimatorContract.AccessoriesTable.CONTENT_LIST_TYPE;
            case ACCESSORIES_ID:
                return JobEstimatorContract.AccessoriesTable.CONTENT_ITEM_TYPE;
            case JOB:
                return JobEstimatorContract.JobTable.CONTENT_LIST_TYPE;
            case JOB_ID:
                return JobEstimatorContract.JobTable.CONTENT_ITEM_TYPE;
            case JOB_NUMBER:
                return JobEstimatorContract.JobNumberTable.CONTENT_LIST_TYPE;
            case JOB_NUMBER_ID:
                return JobEstimatorContract.JobNumberTable.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return JobEstimatorContract.CategoryTable.CONTENT_LIST_TYPE;
            case CATEGORY_ID:
                return JobEstimatorContract.CategoryTable.CONTENT_ITEM_TYPE;
            case SUPPLIER:
                return JobEstimatorContract.SupplierTable.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return JobEstimatorContract.SupplierTable.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
