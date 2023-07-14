package com.gj.fr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gj.arcoredraw.R
import kotlinx.android.synthetic.main.activity_teach.tv_in
import kotlinx.android.synthetic.main.activity_teach.tv_video


class TeachActivity : AppCompatActivity(R.layout.activity_teach) {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TeachActivity::class.java))
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tv_in.setOnClickListener {  }
        tv_video.setOnClickListener {

        }
    }
}