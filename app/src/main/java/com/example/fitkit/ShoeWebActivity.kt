package com.example.fitkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_shoe_web.*

class ShoeWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoe_web)


        kivi_web.webViewClient = WebViewClient()
//        kivi_web.webChromeClient = WebChromeClient()

        kivi_web.settings.javaScriptEnabled = true
        kivi_web.settings.pluginState = WebSettings.PluginState.ON
        kivi_web.settings.setAppCacheEnabled(true)
//        Log.d("kivi",intent.getStringExtra("shoe try on link").toString())
        kivi_web.loadUrl(intent.getStringExtra("shoe try on link").toString())
//        kivi_web.settings.pluginState = WebSettings.PluginState.ON
//        kivi_web.settings.setAppCacheEnabled(true)


    }
}