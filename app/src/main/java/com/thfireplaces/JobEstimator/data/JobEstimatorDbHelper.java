package com.thfireplaces.JobEstimator.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//TODO Add the means to copy the database to a public area for downloading

public class JobEstimatorDbHelper extends SQLiteAssetHelper {
    private static final String LOG_TAG = JobEstimatorDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    //private static String DATABASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + MainActivity.PACKAGE_NAME + "/databases/";
    //private static String DATABASE_PATH = getExternalStorageDirectory().toString();

    private static final String DATABASE_NAME = "jobestimator.db";
    private SQLiteDatabase myDataBase;
    private Context myContext;

    JobEstimatorDbHelper(Context context) {

        super(context, DATABASE_NAME, null, null, DATABASE_VERSION);
        myContext = context;
        //String filePath = context.getExternalFilesDir(null).getAbsolutePath();
        //String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + MainActivity.PACKAGE_NAME ;
        //AssetManager assetsId = myContext.getAssets();
        //String assetsPath = assetsId.;
        Log.v(LOG_TAG, DATABASE_NAME);
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        //boolean dbExist = false;

        if (!dbExist) {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }
        //do nothing - database already exist

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        //return checkDB != null;
        return false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DATABASE_NAME);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
//Open the database
        myDataBase = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
}
