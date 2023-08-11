package com.vishalag53.mytasks.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vishalag53.mytasks.MainActivity
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)

        binding.LogIn.setOnClickListener { logInAction() }

        binding.SignUp.setOnClickListener { signUpAction() }

        binding.forgetPasswordBtn.setOnClickListener {
            Toast.makeText(this, "Forget Password", Toast.LENGTH_SHORT).show()
        }

        binding.logInBtn.setOnClickListener {
            Toast.makeText(this, "Log In", Toast.LENGTH_SHORT).show()
        }

        binding.logInGoogle.setOnClickListener {
            Toast.makeText(this, "Log In Google", Toast.LENGTH_SHORT).show()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.logInBtn.setOnClickListener {
            logInBtn()
        }

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOption)

        binding.logInByGoogle.setOnClickListener {
            logInGoogleBtn()
        }

    }


    private fun logInGoogleBtn() {
        val logInIntent = googleSignInClient.signInIntent
        launcher.launch(logInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }
        else{
            Log.d("VISHAL AGRAWAL","task ${task.exception.toString()}")
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val  intent: Intent = Intent(this,MainActivity::class.java)
                intent.putExtra("email",account.email)
                intent.putExtra("name",account.displayName)
                intent.putExtra("displayImage",account.photoUrl)
                startActivity(intent)
            }
            else{
                Log.d("VISHAL AGRAWAL","${it.exception.toString()}")
            }
        }
    }


    private fun logInBtn() {
        when (binding.logInBtn.text){
            "Log In" -> {
                val email = binding.eMail.text.toString().trim()
                val password = binding.passwords.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                        }
                        else{
                            Toast.makeText(this,"${it.exception.toString()}",Toast.LENGTH_SHORT).show()
                            Log.d("VISHAL AGRAWAL","${it.exception.toString()}")
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Empty Fiels are not Allowed !!",Toast.LENGTH_SHORT).show()
                }

            }
            "Sign Up" ->{
                val email = binding.eMails.text.toString().trim()
                val password = binding.passwordss.text.toString().trim()
                val confirmPassword = binding.passwords01.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                    if(password == confirmPassword){
                        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                            if (it.isSuccessful){
                                startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                            }
                            else{
                                Toast.makeText(this,"${it.exception.toString()}",Toast.LENGTH_SHORT).show()
                                Log.d("VISHAL AGRAWAL","${it.exception.toString()}")
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Password is Not Matching",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"Empty Fiels are not Allowed !!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun signUpAction() {
        binding.logInLayout.visibility = View.GONE
        binding.singUpLayout.visibility = View.VISIBLE
        binding.SignUp.background =
            ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.SignUp.setTextColor(resources.getColor(R.color.black, null))
        binding.LogIn.background = null
        binding.LogIn.setTextColor(resources.getColor(R.color.pinkColor, null))
        binding.logInBtn.setText(R.string.signUp)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun logInAction() {
        binding.singUpLayout.visibility = View.GONE
        binding.logInLayout.visibility = View.VISIBLE
        binding.SignUp.background = null
        binding.SignUp.setTextColor(resources.getColor(R.color.pinkColor, null))
        binding.LogIn.background =
            ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.LogIn.setTextColor(resources.getColor(R.color.black, null))
        binding.logInBtn.setText(R.string.logIn)
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null){
            startActivity(Intent(this@LogInActivity, MainActivity::class.java))
        }
    }
}