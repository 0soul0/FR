package com.gj.fr

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gj.arcoredraw.R
import com.gj.fr.ArActivity.Companion.frModel
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class ResultActivity : AppCompatActivity(R.layout.activity_result) {


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ResultActivity::class.java))
        }
    }

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
            itv_camera.setOnClickListener {
                takePic()
            }
        }
        itv_back.setOnClickListener {
            finish()
        }
    }


    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    val REQUEST_IMAGE_CAPTURE = 1
    private fun takePic() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            //取得檔案絕對位置
            var absolutePath =
                GetFilePathFromUri.getFileAbsolutePath(this, data?.data)
            try {
//                val values = ContentValues()
//                values.put(MediaStore.Images.Media.DATA, absolutePath)
//                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//                saveImageToGallery(contentResolver, absolutePath)
                galleryAddPic()
                SaveDialog().show(supportFragmentManager, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

//    fun saveImageToGallery(cr: ContentResolver, imagePath: String?) {
//        val title = "Saved From Glance"
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, title)
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
//        values.put(MediaStore.Images.Media.DESCRIPTION, title)
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        // Add the date meta data to ensure the image is added at the front of the gallery
//        val millis = System.currentTimeMillis()
//        values.put(MediaStore.Images.Media.DATE_ADDED, millis / 1000L)
//        values.put(MediaStore.Images.Media.DATE_MODIFIED, millis / 1000L)
//        values.put(MediaStore.Images.Media.DATE_TAKEN, millis)
//        var url: Uri? = null
//        try {
//            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//            if (imagePath != null) {
//                val BUFFER_SIZE = 1024
//                val fileStream = FileInputStream(imagePath)
//                try {
//                    val imageOut = cr.openOutputStream(url)
//                    try {
//                        val buffer = ByteArray(BUFFER_SIZE)
//                        while (true) {
//                            val numBytesRead = fileStream.read(buffer)
//                            if (numBytesRead <= 0) {
//                                break
//                            }
//                            imageOut.write(buffer, 0, numBytesRead)
//                        }
//                    } finally {
//                        imageOut.close()
//                    }
//                } finally {
//                    fileStream.close()
//                }
//                val id = ContentUris.parseId(url)
//                // Wait until MINI_KIND thumbnail is generated.
//                val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(
//                    cr,
//                    id,
//                    MediaStore.Images.Thumbnails.MINI_KIND,
//                    null
//                )
//                // This is for backward compatibility.
//                storeThumbnail(cr, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
//            } else {
//                cr.delete(url, null, null)
//            }
//        } catch (e: java.lang.Exception) {
//            if (url != null) {
//                cr.delete(url, null, null)
//            }
//        }
//    }
//
//    private fun storeThumbnail(
//        cr: ContentResolver,
//        source: Bitmap,
//        id: Long,
//        width: Float,
//        height: Float,
//        kind: Int
//    ): Bitmap? {
//
//        // create the matrix to scale it
//        val matrix = Matrix()
//        val scaleX = width / source.width
//        val scaleY = height / source.height
//        matrix.setScale(scaleX, scaleY)
//        val thumb = Bitmap.createBitmap(
//            source, 0, 0,
//            source.width,
//            source.height, matrix,
//            true
//        )
//        val values = ContentValues(4)
//        values.put(MediaStore.Images.Thumbnails.KIND, kind)
//        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
//        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
//        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)
//        val url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)
//        return try {
//            val thumbOut = cr.openOutputStream(url)
//            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
//            thumbOut.close()
//            thumb
//        } catch (ex: FileNotFoundException) {
//            null
//        } catch (ex: IOException) {
//            null
//        }
//    }
}