package com.thfireplaces.JobEstimator

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import com.thfireplaces.JobEstimator.data.JobEstimatorContract
import kotlinx.android.synthetic.main.activity_take_picture.*
import java.io.File
import java.io.IOException

class TakePicture : AppCompatActivity() {

    private val requestImageCapture = 1
    private lateinit var mImageView: ImageView
    private lateinit var mCurrentPhotoPath: String
    private lateinit var mCurrentJobUri: Uri
    private lateinit var mJobPrefix: String
    private lateinit var timeStamp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        title = "Take Picture"
        setContentView(R.layout.activity_take_picture)
        mImageView = takepicture

        mCurrentJobUri = intent.data

        mJobPrefix = intent.getStringExtra("jobprefix")

        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Toast.makeText(this, "Problem creating Image file", Toast.LENGTH_LONG).show()
                finish()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        "com.thfireplaces.JobEstimator.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, requestImageCapture)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestImageCapture && resultCode == Activity.RESULT_OK) {
            setPic()
            addPictureToJob(mCurrentJobUri, timeStamp)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        timeStamp = Utils.getCurrentDate(true)
        val imageFileName = "${mJobPrefix}_$timeStamp"
        val storageDir = getExternalFilesDir(DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        val nFile = File("$storageDir/$imageFileName.jpg")
        Log.v("TakePicture", " file name: " + nFile.name)
        image.renameTo(nFile)
        mCurrentPhotoPath = nFile.absolutePath

        return nFile
    }

    private fun setPic() {
        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = 1
        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        mImageView.setImageBitmap(bitmap)
        mImageView.visibility = VISIBLE
    }

    private fun addPictureToJob(uri: Uri, timestamp: String) {
        var pictures: String = Utils.getJobPictures(this, uri)
        val values = ContentValues()

        pictures = pictures.trim() + "$timestamp:"

        values.put(JobEstimatorContract.JobTable.COL_PICTURES, pictures)
        Utils.updateJobWithContent(this, values, mCurrentJobUri)

    }
}
