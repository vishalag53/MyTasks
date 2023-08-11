package com.vishalag53.mytasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.vishalag53.mytasks.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userName = intent.getStringExtra("name")
        val userEmail = intent.getStringExtra("email")
        val userDisplayPhoto = intent.getStringExtra("displayImage")
    }

}