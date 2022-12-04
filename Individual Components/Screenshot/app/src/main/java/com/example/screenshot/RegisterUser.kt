package com.example.screenshot

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterUser : AppCompatActivity(), View.OnClickListener {

    // Variables to be used
    private lateinit var auth: FirebaseAuth
    private lateinit var banner: TextView
    private lateinit var register: TextView
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        // Initialize Variables
        auth = Firebase.auth
        banner = findViewById(R.id.banner)
        register = findViewById(R.id.register)
        firstName = findViewById(R.id.firstname)
        lastName = findViewById(R.id.lastname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)

        // Establish listeners for clicks
        register.setOnClickListener(this)
        banner.setOnClickListener(this)
    }

    /*
    Used to take us back to the main activity screen after clicking on the title Planet.com
     */
    private fun launchBannerActivity() {
        val bannerIntent = Intent(this, MainActivity::class.java)
        startActivity(bannerIntent)
    }

    // Observe which view was click and perform logic depending on selection
    override fun onClick(view: View) {
        when (view.id) {
            R.id.banner -> launchBannerActivity()
            R.id.register -> registerUser()
        }
    }

    /*
    Used to register a new user in the firebase. Every field must be filled out to ensure the user is
    added accurately and they can login after registration
     */
    private fun registerUser() {

        //Form string from all the text in the text fields
        val firstNameString = firstName.text.toString().trim()
        val lastNameString = lastName.text.toString().trim()
        val emailString = email.text.toString().trim()
        val passwordString = password.text.toString().trim()

        // Post an error message if the name, password, or email is empty
        if (firstNameString.isEmpty()) {
            firstName.error = "First Name is Required!"
            firstName.requestFocus()
            return
        }

        if (lastNameString.isEmpty()) {
            lastName.error = "Last Name is Required!"
            lastName.requestFocus()
            return
        }

        if (emailString.isEmpty()) {
            email.error = ("Email is Required!")
            email.requestFocus()
            return
        }

        // Check that a valid email was entered
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = "Please Provide a Valid Email"
            email.requestFocus()
            return
        }

        if (passwordString.isEmpty()) {
            password.error = "Password is required!"
            password.requestFocus()
            return
        }

        // Check that the length of the password is 8 or more characters
        if (passwordString.length < 8) {
            password.error = "Password Must Be at at least 8 Characters long"
            password.requestFocus()
            return
        }

        // Display progressbar
        progressBar.visibility = View.VISIBLE

        // Create a new user
        auth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    //Use user constructor to make new user
                    val user = User(firstNameString, lastNameString, emailString)

                    // Set a user ID by getting the instance of the Users tab in the firebase
                    // and storing the current(new) user as a child
                    FirebaseDatabase.getInstance().getReference("Users").child(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    ).setValue(user).addOnCompleteListener { task ->

                        // Upon Success
                        if (task.isSuccessful) {
                            // Display success message
                            Toast.makeText(
                                this, "User has been registered!",
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                this, "Failed to register! Please Try Again",
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(
                        this, "Failed to register! Please Try Again",
                        Toast.LENGTH_LONG
                    ).show()

                    // Once everything is done, hide the progress bar
                    progressBar.visibility = View.GONE
                }

            }

    }
}