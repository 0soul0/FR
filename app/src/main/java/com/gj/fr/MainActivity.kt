package com.gj.fr


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.gj.arcoredraw.R


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initOnClickListener()
    }


    private fun initOnClickListener() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            ArActivity.start(this)
        }

        findViewById<Button>(R.id.btn_teach).setOnClickListener {
            SettingActivity.start(this)
        }
    }


    fun permissionPhoto() {
        ActivityCompat.requestPermissions(
            this,arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), 0
        )
    }

}