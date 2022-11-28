package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    // text to enter email and button to reset Password
    private lateinit var email: EditText
    private lateinit var passwordButton: Button
    private lateinit var banner: TextView

    // Progressbar and access data from firebase
    private lateinit var progressBar: ProgressBar
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize variables from forget_password.xml
        email = findViewById(R.id.email)
        passwordButton = findViewById(R.id.resetPassword)
        progressBar = findViewById(R.id.progressBar)
        banner = findViewById(R.id.banner)
        auth = FirebaseAuth.getInstance()

        // Listen for when the button is clicked
        passwordButton.setOnClickListener(this)
        banner.setOnClickListener(this)
    }

    // Call resetPassword
    override fun onClick(view: View) {
        when(view.id) {
            R.id.forgotPassword -> resetPassword()
            R.id.banner -> launchBannerActivity()
        }
    }

    /*
    Used to take us back to the main activity screen after clicking on the title Planet.com
     */
    private fun launchBannerActivity() {
        val bannerIntent = Intent(this, MainActivity::class.java)
        startActivity(bannerIntent)
    }

    /*
    This function will check that the email given is valid then send a link to the users email to
    reset their password if they forgot it. It will update the database instantly to allow for
    an immediate login
     */
    private fun resetPassword() {
        val emailString = email.text.toString().trim()

        // Check that the email is not empty.
        if (emailString.isEmpty()) {
            // Display error message if the email is empty
            email.error = "Email is required!"
            email.requestFocus()
            return
        }
        // Check the email matches what is in the database
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = "Please provide correct email associated with account"
            email.requestFocus()
            return
        }
        // Show progressbar while password reset is being sent.
        progressBar.visibility = View.VISIBLE

        // Send a password reset email
        auth.sendPasswordResetEmail(emailString).addOnCompleteListener { task ->

            // Upon success
            if (task.isSuccessful) {
                Toast.makeText(
                    this, "A password reset link was set to your email.",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.GONE
            } else {
                Toast.makeText(
                    this, "Something wrong happened.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}