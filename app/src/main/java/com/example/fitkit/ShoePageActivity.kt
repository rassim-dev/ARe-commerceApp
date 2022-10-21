package com.example.fitkit

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_shoe_page.*
/**
 * Big shout-out for Pratap Singh on stack overflow and geeksforgeeks for helping me and others
 * overcome this issue (opening chrome tabs within the app seamlessly)
 * reference: https://stackoverflow.com/questions/57200229/android-webview-camera-and-microphone-permission-granted-but-cannot-access#:~:text=I%20too%20faced,the%20time%20being.
 * reference: https://www.geeksforgeeks.org/chrome-custom-tabs-in-android-with-kotlin/
 * reference: https://developer.chrome.com/docs/android/custom-tabs/integration-guide/
*/
class ShoePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoe_page)

        Glide.with(this).load(intent.getStringExtra("shoe img")).placeholder(R.drawable.placeholder).into(shoe_img)
        shoe_name_tv.text = intent.getStringExtra("shoe name")
        shoe_price_tv.text = intent.getStringExtra("shoe price")
        shoe_desc_tv.text = "•Colors: "+intent.getStringExtra("shoe colors")+"\n•"+intent.getStringExtra("shoe desc")

    }

    fun toTryItOn(v: View){
        val kivi_link = intent.getStringExtra("kivi link")

        val builder = CustomTabsIntent.Builder()

        // to set the toolbar color use CustomTabColorSchemeParams
        val params = CustomTabColorSchemeParams.Builder()
        val colorInt = Color.parseColor("#464D83")
        params.setToolbarColor(colorInt)
        builder.setDefaultColorSchemeParams(params.build())

        // shows the title of web-page in toolbar
        builder.setShowTitle(false)

        // setShareState(CustomTabsIntent.SHARE_STATE_ON) will add a menu to share the web-page
        builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

        // to set weather instant apps is enabled for the custom tab or not, use
        builder.setInstantAppsEnabled(true)

        val customBuilder = builder.build()
        customBuilder.intent.setPackage("com.android.chrome")
        customBuilder.launchUrl(this, Uri.parse(kivi_link))


    }
}