package com.vishalag53.mytasks.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.vishalag53.mytasks.MainActivity
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.ActivityLogInBinding
import java.lang.Exception

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

        firebaseAuth = FirebaseAuth.getInstance()

        binding.logInBtn.setOnClickListener { logInBtn() }

        binding.forgetPasswordBtn.setOnClickListener { forgetPasswordAction() }

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOption)

        binding.logInGoogle.setOnClickListener { logInGoogleBtn() }
    }

    //  Google LogIn Api

    private fun logInGoogleBtn() {
        val logInIntent = googleSignInClient.signInIntent
        launcher.launch(logInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val  intent: Intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email",account.email)
                intent.putExtra("name",account.displayName)
                intent.putExtra("displayImage",account.photoUrl)
                startActivity(intent)
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
                            isEmailVerified()
                        }
                        else{
                            Toast.makeText(this,"Email and Password are wrong!!",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Empty Fields are not Allowed !!",Toast.LENGTH_SHORT).show()
                }

            }
            "Sign Up" ->{
                val email = binding.eMails.text.toString().trim()
                val password = binding.passwordss.text.toString().trim()
                val confirmPassword = binding.passwords01.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                    if(password == confirmPassword){
                        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful){
                                val user = firebaseAuth.currentUser!!
                                sendVerificationEmail(user)
                                startActivity(Intent(this@LogInActivity,LogInActivity::class.java))
                            }
                            else{
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseAuthInvalidUserException){
                                    Toast.makeText(this,"Invalid Email Id",Toast.LENGTH_SHORT).show()
                                } catch (e: FirebaseAuthInvalidCredentialsException){
                                    Toast.makeText(this,"Invalid Password",Toast.LENGTH_SHORT).show()
                                } catch (e: FirebaseAuthUserCollisionException){
                                    Toast.makeText(this,"User Already exists",Toast.LENGTH_SHORT).show()
                                } catch (e:Exception){
                                    Toast.makeText(this,"Invalid Email and Password",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Password is Not Matching",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"Empty Fields are not Allowed !!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isEmailVerified() {
        val verification = firebaseAuth.currentUser?.isEmailVerified
        if(verification == true){
            startActivity(Intent(this@LogInActivity, MainActivity::class.java))
        }
        else{
            Toast.makeText(this, "Verify a Email Id", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(this, "Please verify your Email", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "You have 5 minutes to verify a Email!", Toast.LENGTH_SHORT).show()
            }

        Handler(Looper.getMainLooper()).postDelayed({
            val verification = firebaseAuth.currentUser!!.isEmailVerified
            if(verification){}
            else{
                user.delete()
            }
        },120000)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun signUpAction() {
        binding.logInLayout.visibility = View.INVISIBLE
        binding.singUpLayout.visibility = View.VISIBLE
        binding.SignUp.background = ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.SignUp.setTextColor(resources.getColor(R.color.black, null))
        binding.LogIn.background = null
        binding.LogIn.setTextColor(resources.getColor(R.color.pinkColor, null))
        binding.logInBtn.setText(R.string.signUp)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun logInAction() {
        binding.singUpLayout.visibility = View.INVISIBLE
        binding.logInLayout.visibility = View.VISIBLE
        binding.SignUp.background = null
        binding.SignUp.setTextColor(resources.getColor(R.color.pinkColor, null))
        binding.LogIn.background = ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.LogIn.setTextColor(resources.getColor(R.color.black, null))
        binding.logInBtn.setText(R.string.logIn)
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null){
            isEmailVerified()
        }
    }

    private fun forgetPasswordAction(){
        val email = binding.textInputEmail.editText?.text.toString()
        if (email.isNotEmpty()){
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this,"Failed to send password reset email",Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else{
            Toast.makeText(this, "Please Enter the Email Id", Toast.LENGTH_SHORT).show()
        }
    }
}