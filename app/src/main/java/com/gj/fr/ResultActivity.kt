package com.gj.fr

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.gj.arcoredraw.R
import com.gj.fr.ArActivity.Companion.frModel
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity: AppCompatActivity(R.layout.activity_result) {


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
            tv_out.text=it.out+"mm"
            tv_hole.text=it.hole+"mm"
            tv_thiness.text=it.thickness+"mm"
            tv_screw.text=it.screw
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

    private fun takePic() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            //取得檔案絕對位置
            var absolutePath =
                GetFilePathFromUri.getFileAbsolutePath(this, data?.data)
            try {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, absolutePath)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                SaveDialog().show(supportFragmentManager, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}