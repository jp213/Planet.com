package com.example.loginscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var logout : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser
    private lateinit var reference : DatabaseReference
    private lateinit var userID : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        logout = findViewById(R.id.signOut)
        auth = FirebaseAuth.getInstance()
        logout.setOnClickListener(this)
        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users")
        userID = user.uid
        val welcomeTextView = findViewById<TextView>(R.id.welcome)
        val firstNameTextView = findViewById<TextView>(R.id.firstName)
        val emailTextView = findViewById<TextView>(R.id.emailAddress)
        val lastNameTextView = findViewById<TextView>(R.id.lastName)
        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(User::class.java)
                if (userProfile != null) {
                    val firstName = userProfile.firstName
                    val lastName = userProfile.lastName
                    val email = userProfile.email

                    welcomeTextView.text = "Welcome, $firstName $lastName!"
                    firstNameTextView.text = firstName
                    lastNameTextView.text = lastName
                    emailTextView.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Uh oh, an error occurred", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onClick(view: View) {
        auth.signOut()
        launchLogoutActivity()

    }

    private fun launchLogoutActivity() {
        val logOutIntent = Intent(this,MainActivity::class.java)
        startActivity(logOutIntent)
    }
}