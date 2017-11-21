package com.thfireplaces.JobEstimator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.thfireplaces.JobEstimator.data.JobEstimatorContract.JobTable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions
 */

class Utils2 {
    companion object {


        fun decodeEntries(input: String, delimiter: Char): ArrayList<String> {
            val value = ArrayList<String>()

            if (!TextUtils.isEmpty(input)) {
                var start = 0
                var idx: Int
                idx = input.indexOf(delimiter, start)
                while (idx != -1) {
                    val sub = input.substring(start, idx)
                    value.add(sub)
                    start = idx + 1
                    idx = input.indexOf(delimiter, start)
                }
            }
            return value
        }

         fun getCurrentDate(includeTime: Boolean): String {
            val mdformat: SimpleDateFormat = when {
                !includeTime -> SimpleDateFormat("yyyy-MM-dd ", Locale.CANADA)
                else -> SimpleDateFormat("yyyyMMddHHmmss", Locale.CANADA)
            }

            return mdformat.format(Calendar.getInstance().time)
        }

        fun get_job_selection(context: Context, mCurrentJobUri: Uri): ArrayList<String> {
            val jobCursor: Cursor?
            var materialCodes = ArrayList<String>()
            val projection = arrayOf(JobTable._ID, JobTable.COL_PRODUCT_SELECTIONS)
            jobCursor = context.contentResolver.query(
                    mCurrentJobUri,
                    projection,
                    null, null, null
            )
            if (jobCursor != null) {
                if (jobCursor.moveToFirst()) {
                    var mSelections = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PRODUCT_SELECTIONS))
                    if (!TextUtils.isEmpty(mSelections)) mSelections = mSelections.trim { it <= ' ' }

                    materialCodes.clear()
                    if (!TextUtils.isEmpty(mSelections)) {
                        materialCodes = decodeEntries(mSelections, ':')
                    }
                }
                jobCursor.close()
            }
            return materialCodes
        }

        fun get_job_pictures(context: Context, mCurrentJobUri: Uri): String {
            var pictures = ""
            val projection = arrayOf(JobTable._ID, JobTable.COL_PICTURES)
            val jobCursor = context.contentResolver.query(
                    mCurrentJobUri,
                    projection, null, null, null
            )
            if (jobCursor != null) {
                if (jobCursor.moveToFirst()) {
                    pictures = jobCursor.getString(jobCursor.getColumnIndexOrThrow(JobTable.COL_PICTURES))
                    pictures = if (!TextUtils.isEmpty(pictures))
                        pictures.trim { it <= ' ' }
                    else
                        " "
                }
                jobCursor.close()
            }
            return pictures
        }

        fun updateJobWithContent(ctx: Context, values: ContentValues, jobUri: Uri) {

            val nRows = ctx.contentResolver.update(jobUri,
                    values, null, null)
            if (nRows == 0) { // no rows - empty jobnumber table.  make the first entry
                Log.v("Utils", "Failed to update job_table with new values" + values.toString())
            }
        }
    }

}
