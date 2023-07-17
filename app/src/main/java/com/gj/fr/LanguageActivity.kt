package com.gj.fr

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.gj.arcoredraw.R
import kotlinx.android.synthetic.main.activity_language.itv_back
import kotlinx.android.synthetic.main.activity_language.tv_chinese
import kotlinx.android.synthetic.main.activity_language.tv_english
import java.util.Locale


class LanguageActivity : AppCompatActivity(R.layout.activity_language) {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LanguageActivity::class.java))
        }
    }

    private var TAG: String = "TeachActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itv_back.setOnClickListener {
            finish()
        }
        val res = this.resources
        val dm = res.displayMetrics
        val config: Configuration = res.configuration
        tv_english.setOnClickListener {
            config.locale= Locale.ENGLISH
            res.updateConfiguration(config, dm)
            restartApp()
        }
        tv_chinese.setOnClickListener {
            config.locale= Locale.CHINESE
            res.updateConfiguration(config, dm)
            restartApp()
        }
    }

    fun restartApp(){
        Handler().postDelayed(object :Runnable {
            override fun run() {
               val intent= packageManager.getLaunchIntentForPackage(application.packageName)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        },100)
    }

}