package com.example.loginscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    // Variables for buttons, Firebase, and Database
    private lateinit var logout: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize variables
        logout = findViewById(R.id.signOut)
        auth = FirebaseAuth.getInstance()
        logout.setOnClickListener(this)
        user = FirebaseAuth.getInstance().currentUser!! // Null check
        reference = FirebaseDatabase.getInstance().getReference("Users")
        userID = user.uid

        // Create TextView variables from profile.xml
        val welcomeTextView = findViewById<TextView>(R.id.welcome)
        val firstNameTextView = findViewById<TextView>(R.id.firstName)
        val emailTextView = findViewById<TextView>(R.id.emailAddress)
        val lastNameTextView = findViewById<TextView>(R.id.lastName)

        // Pull name and email from the firebase to display on the screen
        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Use empty constructor to get name and email from Firebase
                val userProfile = snapshot.getValue(User::class.java)
                if (userProfile != null) {
                    // Update name and email
                    val firstName = userProfile.firstName
                    val lastName = userProfile.lastName
                    val email = userProfile.email

                    // Update Screen with information in the User constructor
                    welcomeTextView.text = "Welcome, $firstName $lastName!"
                    firstNameTextView.text = firstName
                    lastNameTextView.text = lastName
                    emailTextView.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // If the data could not be pulled
                Toast.makeText(this@ProfileActivity, "Uh oh, an error occurred", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    override fun onClick(view: View) {
        // Log the current user out
        auth.signOut()
        launchLogoutActivity()

    }

    /*
    This function is used to log the current user out of the session and go back to the home login
    screen. An Intent is created to take us back to the MainActivity screen
     */
    private fun launchLogoutActivity() {
        val logOutIntent = Intent(this, MainActivity::class.java)
        startActivity(logOutIntent)
    }
}