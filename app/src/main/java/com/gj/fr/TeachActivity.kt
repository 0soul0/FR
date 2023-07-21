package com.gj.fr

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gj.arcoredraw.R
import kotlinx.android.synthetic.main.activity_teach.itv_back
import kotlinx.android.synthetic.main.activity_teach.tv_in
import kotlinx.android.synthetic.main.activity_teach.tv_video


class TeachActivity : AppCompatActivity(R.layout.activity_teach) {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TeachActivity::class.java))
        }
    }

    private var TAG: String = "TeachActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        pdfView.visibility=View.GONE
        tv_in.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.fidesbio.com/images/Flange_AR_cht.pdf"))
            startActivity(intent)
//            openPdfFromRawResource(R.raw.manual_z)
        }
        tv_video.setOnClickListener {
//            VideoActivity.start(this)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.fidesbio.com/images/Flange_AR_cht.mp4"))
            startActivity(intent)
        }

        itv_back.setOnClickListener {
            finish()
        }
    }

    private fun openPdfFromRawResource(rawResourceId: Int) {
        val pdfUri = Uri.parse("android.resource://$packageName/$rawResourceId")
//        pdfView.visibility=View.VISIBLE
//        pdfView.fromUri(pdfUri).load()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, resources.getString(R.string.can_not_open_pdf),Toast.LENGTH_LONG)
        }
    }
}