package com.vishalag53.mytasks.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.vishalag53.mytasks.MainActivity
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_log_in)

        binding.backBtn.setOnClickListener {
            backBtnAction()
        }

        binding.LogIn.setOnClickListener {
            logInAction()
        }

        binding.SignUp.setOnClickListener {
            signUpAction()
        }

    }

    private fun backBtnAction() {
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Not Sign In")
            .setMessage("Are you sure, don't want to log in or sign in")
            .setPositiveButton("YES"){ dialog, _ ->
                startActivity(Intent(this@LogInActivity,MainActivity::class.java))
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)

        alertDialogBuilder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun signUpAction() {
        binding.logInLayout.visibility = View.GONE
        binding.singUpLayout.visibility = View.VISIBLE
        binding.SignUp.background = ResourcesCompat.getDrawable(resources,R.drawable.switch_trcks,null)
        binding.SignUp.setTextColor(resources.getColor(R.color.black,null))
        binding.LogIn.background = null
        binding.LogIn.setTextColor(resources.getColor(R.color.pinkColor,null))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun logInAction() {
        binding.singUpLayout.visibility = View.GONE
        binding.logInLayout.visibility = View.VISIBLE
        binding.SignUp.background = null
        binding.SignUp.setTextColor(resources.getColor(R.color.pinkColor,null))
        binding.LogIn.background = ResourcesCompat.getDrawable(resources,R.drawable.switch_trcks,null)
        binding.LogIn.setTextColor(resources.getColor(R.color.black,null))
    }
}