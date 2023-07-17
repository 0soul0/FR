package com.gj.fr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.gj.arcoredraw.R
import kotlinx.android.synthetic.main.activity_video.itv_back
import kotlinx.android.synthetic.main.activity_video.videoView


class VideoActivity : AppCompatActivity(R.layout.activity_video) {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VideoActivity::class.java))
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = Uri.parse("android.resource://$packageName/${R.raw.video_teach}")
        videoView.setVideoURI(url)
        videoView.setMediaController(MediaController(this));
        videoView.start()

        itv_back.setOnClickListener {
            finish()
        }
    }
}