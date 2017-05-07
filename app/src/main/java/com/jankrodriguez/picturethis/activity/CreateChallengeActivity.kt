package com.jankrodriguez.picturethis.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.jankrodriguez.picturethis.R
import com.jankrodriguez.picturethis.model.CreateChallengeBody
import com.jankrodriguez.picturethis.service.PICTURE_THIS_SERVICE_INSTANCE
import com.jankrodriguez.picturethis.sharedprefs.getUser
import com.jankrodriguez.picturethis.sharedprefs.getUserSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_challenge.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_IMAGE_CAPTURE = 1
private val TAG = CreateChallengeActivity::class.java.simpleName

class CreateChallengeActivity : AppCompatActivity() {

  private var currentPhotoPath: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_challenge)

    supportActionBar?.setTitle(R.string.create_challenge_title)

    createTakePicture.setOnClickListener {
      takePicture()
    }

    createPublicSwitch.setOnCheckedChangeListener { _, isChecked ->
      val visibility = if (isChecked) View.GONE else View.VISIBLE
      createFriendsList.visibility = visibility
      createSelectFriends.visibility = visibility
    }

    createChallengeBtn.setOnClickListener {
      uploadImageAndCreateChallenge()
    }
  }

  private fun takePicture() {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (takePictureIntent.resolveActivity(packageManager) != null) {
      // Create the File where the photo should go
      val photoFile: File
      try {
        photoFile = createImageFile()
      } catch (ex: IOException) {
        Log.e(localClassName, "Error taking pic", ex)
        return
      }
      val photoURI = FileProvider.getUriForFile(this,
          "com.jankrodriguez.picturethis.fileprovider",
          photoFile)

      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
      if (currentPhotoPath != null) {
        // Get the dimensions of the View
        val targetW = createTakePicture.width
        val targetH = createTakePicture.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        createTakePicture.setColorFilter(Color.TRANSPARENT)
        createTakePicture.setImageBitmap(bitmap)

      }
    }
  }

  @Throws(IOException::class)
  private fun createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir      /* directory */
    )

    // Save a file: path for use with ACTION_VIEW intents
    currentPhotoPath = image.absolutePath
    return image
  }

  private fun uploadImageAndCreateChallenge() {
    if (validateAllInput() && currentPhotoPath != null) {
      val user = getUserSharedPreferences().getUser()
      val file = File(currentPhotoPath)
//      MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
      val fileRequestBody = MultipartBody.Part.createFormData("upload", file.name, RequestBody.create(MediaType.parse("image/*"), file))
      val title = createTitle.text.toString()
      PICTURE_THIS_SERVICE_INSTANCE.uploadImage(fileRequestBody)
          .flatMap {
            val createChallengeBody = CreateChallengeBody(
                user!!.google_id,
                title,
                0f,
                0f,
                it.filename,
                createPublicSwitch.isChecked)
            PICTURE_THIS_SERVICE_INSTANCE.createChallenge(createChallengeBody)
          }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              {
                Log.d(TAG, it.toString())
              },
              {
                Log.e(TAG, it.message, it)
              })
    }
  }

  private fun validateAllInput(): Boolean {
    if (createTitle.text.isEmpty()) {
      return false
    }

    return true
  }
}
