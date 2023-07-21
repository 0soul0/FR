package com.gj.fr

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.gj.arcoredraw.R
import com.gj.fr.ArActivity.Companion.frModel
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.jar.Manifest


class ResultActivity : AppCompatActivity(R.layout.activity_result) {


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ResultActivity::class.java))
        }
    }

    private val TAG = "ResultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindItem()
    }

    private fun bindItem() {
        frModel?.let {
//            tv_title.text = it.type.substring(0,5)
//            tv_size.text=it.type.substring(0,5)
//            tv_level.text=it.type.substring(0,5)
            tv_out.text = it.out + "mm"
            tv_hole.text = it.hole + "mm"
            tv_thiness.text = it.thickness + "mm"
            tv_screw.text = it.screw
            view_fr.setScrewCount(it.screw.toInt())
//            tv_material.text=it.type
//            tv_standard.text=it.type

        }
        itv_camera.setOnClickListener {
            takePic()
        }
        itv_back.setOnClickListener {
            finish()
        }
    }


    lateinit var currentPhotoPath: String

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }


    val REQUEST_IMAGE_CAPTURE = 1
    private fun takePic() {
        Log.d(TAG, "takePic: ${ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)} ${PackageManager.PERMISSION_GRANTED}")
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionPhoto()
            return
        }

        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE),1)

//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//        }
    }



    private fun permissionPhoto() {
        ActivityCompat.requestPermissions(
            this,arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), 77
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode==77){
            if(grantResults.isNotEmpty() &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                takePic()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            //取得檔案絕對位置
//            var absolutePath = GetFilePathFromUri.getFileAbsolutePath(this, data?.data)
//            try {
////                val values = ContentValues()
////                values.put(MediaStore.Images.Media.DATA, absolutePath)
////                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
////                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
////                saveImageToGallery(contentResolver, absolutePath)
//                galleryAddPic()
//                SaveDialog().show(supportFragmentManager, "")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            val contentResolver = contentResolver
            val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"${System.currentTimeMillis()}.jpg")
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"images/*")
            val uri = contentResolver.insert(images,contentValues)

//
                try {
                    Log.d(TAG, "try: ${ data?.extras}")
                    val extras = data?.extras
                    val bitmap: Bitmap = extras?.get("data") as Bitmap ?: return
                    val outputStream = Objects.requireNonNull(uri)
                        ?.let { contentResolver.openOutputStream(it) }
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                    Objects.requireNonNull(outputStream)

                    SaveDialog().show(supportFragmentManager, "")

                }catch (e:Exception){
                    Log.d(TAG, "onActivityResult: ${e.message}")
                }

        }
    }


    private fun saveImage(){

    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

}