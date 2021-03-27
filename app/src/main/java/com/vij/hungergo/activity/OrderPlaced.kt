package com.vij.hungergo.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.vij.hungergo.R
import kotlinx.android.synthetic.main.activity_order_placed.*

class OrderPlaced : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnOk.setOnClickListener {
            startActivity(Intent(this,RestaurantListActivty::class.java))
            finishAffinity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        startActivity(Intent(this,RestaurantListActivty::class.java))
        finishAffinity()
    }
}
