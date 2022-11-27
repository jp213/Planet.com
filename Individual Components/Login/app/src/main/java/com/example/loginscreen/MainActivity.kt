package com.example.loginscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var register :TextView
    private lateinit var forgotPassword : TextView
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.signIn)
        button.setOnClickListener(this)
        register = findViewById(R.id.register)
        register.setOnClickListener(this)
        forgotPassword = findViewById(R.id.forgotPassword)
        forgotPassword.setOnClickListener(this)
    }

    private fun userLogin() {
        val emailString = email.text.toString().trim()
        val passwordString = password.text.toString().trim()

        if(emailString.isEmpty()) {
            email.error = ("Email is Required!")
            email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = "Please Enter a Valid Email"
            email.requestFocus()
            return
        }

        if (passwordString.isEmpty()) {
            password.error = "Password is required!"
            password.requestFocus()
            return
        }

        if (passwordString.length < 8){
            password.error = "Password Must Be at at least 8 Characters long"
            password.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    if (user.isEmailVerified){
                        launchProfileActivity()
                    }
                    else {
                        user.sendEmailVerification()
                        Toast.makeText(this, "Please check your email to verify account"
                            , Toast.LENGTH_LONG).show()
                    }
                }
            }
            else {
                Toast.makeText(this, "Please check credentials", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun launchRegisterActivity(){
        val registerUserIntent = Intent(this,RegisterUser::class.java)
        startActivity(registerUserIntent)
    }

    private fun launchProfileActivity() {
        val profileIntent = Intent(this,ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.register -> launchRegisterActivity()
            R.id.signIn -> userLogin()
            R.id.forgotPassword -> launchForgotPasswordActivity()
        }
    }

    private fun launchForgotPasswordActivity() {
        val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(forgotPasswordIntent)
    }

}




