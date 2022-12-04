package com.example.screenshot

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var register: TextView
    private lateinit var forgotPassword: TextView

    //Login with email and password
    private lateinit var email: EditText
    private lateinit var password: EditText

    //Connect with firebase, login and show a loading circle when switching activities
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        loginButton = findViewById(R.id.signIn)
        register = findViewById(R.id.register)
        forgotPassword = findViewById(R.id.forgotPassword)

        // Change activity and screen when thse are clicked
        forgotPassword.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        register.setOnClickListener(this)
    }

    /*
    Allow to the user too login and access data which is stored in our app. The user must input the
    correct email and password at the time of registration or they will be instructed to check
    their credentials.
     */
    private fun userLogin() {
        // Initialize email and password to strings with nothing extra leading/trailing
        val emailString = email.text.toString().trim()
        val passwordString = password.text.toString().trim()

        // Check if the email is empty, if so direct the user back to the email prompt
        if (emailString.isEmpty()) {
            email.error = ("Email is Required!")
            email.requestFocus()
            return
        }

        // Check the email matches what is stored in the firebase
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = "Please Enter a Valid Email"
            email.requestFocus()
            return
        }

        // Check that the password is empty
        if (passwordString.isEmpty()) {
            password.error = "Password is required!"
            password.requestFocus()
            return
        }

        // Check that the password is 8 or more characters
        if (passwordString.length < 8) {
            password.error = "Password Must Be at at least 8 Characters long"
            password.requestFocus()
            return
        }

        // Show the progress bar while information is being verified
        progressBar.visibility = View.VISIBLE

        // Verify the proper information was passed into the email and password
        auth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener { task ->
            // Task is accessing the proper account, check if its successful
            if (task.isSuccessful) {
                // Get the current User information
                val user = FirebaseAuth.getInstance().currentUser
                // Check the user is valid
                if (user != null) {
                    // Check that the email was verified via a link sent to email
                    if (user.isEmailVerified) {
                        // Change screens and remove progress bar
                        launchScreenshotActivity()
                        progressBar.visibility = View.GONE
                    } else {
                        // Send email verification and display a message
                        user.sendEmailVerification()
                        Toast.makeText(
                            this, "Please check your email to verify account", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please check credentials", Toast.LENGTH_LONG).show()
            }
        }

    }

    /*
    This is used to create an intent which will change from the home screen to the registration
    screen so the user can register in the app.
     */
    private fun launchRegisterActivity() {
        val registerUserIntent = Intent(this, RegisterUser::class.java)
        startActivity(registerUserIntent)
    }


    private fun launchScreenshotActivity() {
        val i = Intent(this, ScreenshotActivity::class.java)
        startActivity(i)
    }

    /*
    This is used to create an intent which will change from the home screen to the forget password
    screen after clicking on forget password
     */
    private fun launchForgotPasswordActivity() {
        val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(forgotPasswordIntent)
    }

    /*
    Used to determine which screen is needed to transition to depending on how the view changes based
    on the respective clicks.
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.register -> launchRegisterActivity()
            R.id.signIn -> userLogin()
            R.id.forgotPassword -> launchForgotPasswordActivity()
        }
    }



}