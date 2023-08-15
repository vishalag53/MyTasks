package com.vishalag53.mytasks.Tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vishalag53.mytasks.R

@Suppress("DEPRECATION")
class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        val userName = intent.getStringExtra("name")
        val userEmail = intent.getStringExtra("email")
        val userDisplayPhoto = intent.getStringExtra("displayImage")

    }

}