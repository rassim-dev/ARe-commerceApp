package com.example.fitkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun toProductsActivity(view: View){
        val intent = Intent(this, ProductListActivity::class.java)
        startActivity(intent)
    }

    fun toShoeActivity(view: View){
        val intent = Intent(this, ShoeActivity::class.java)
        startActivity(intent)
    }

//    fun toFootActivity(view: View){
//        val intent = Intent(this,FootActivity::class.java)
//        startActivity(intent)
//    }

    fun toGuideActivity(v: View){
        val intent = Intent(this,GuidelinesActivity::class.java)
        startActivity(intent)
    }

    fun toContactActivity(v: View){
        val intent = Intent(this,ContactActivity::class.java)
        startActivity(intent)
    }
}