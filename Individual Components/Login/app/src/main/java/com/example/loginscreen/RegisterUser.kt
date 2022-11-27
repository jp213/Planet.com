package com.example.loginscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterUser : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var banner : TextView
    private lateinit var register: TextView
    private lateinit var firstName : EditText
    private lateinit var lastName : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        auth = Firebase.auth
        banner = findViewById(R.id.banner)
        banner.setOnClickListener(this)
        register = findViewById(R.id.register)
        register.setOnClickListener(this)
        firstName = findViewById(R.id.firstname)
        lastName = findViewById(R.id.lastname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        progressBar = findViewById(R.id.progressBar)
    }

    private fun launchBannerActivity(){
        val bannerIntent = Intent(this,MainActivity::class.java)
        startActivity(bannerIntent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.banner -> launchBannerActivity()
            R.id.register -> registerUser()
        }
    }

    private fun registerUser() {
        val firstNameString = firstName.text.toString().trim()
        val lastNameString = lastName.text.toString().trim()
        val emailString = email.text.toString().trim()
        val passwordString = password.text.toString().trim()

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

        if(emailString.isEmpty()) {
            email.error = ("Email is Required!")
            email.requestFocus()
            return
        }
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
        if (passwordString.length < 8){
            password.error = "Password Must Be at at least 8 Characters long"
            password.requestFocus()
            return
        }
        progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                val user = User(firstNameString, lastNameString, emailString)
                FirebaseDatabase.getInstance().getReference("Users").child(
                    FirebaseAuth.getInstance().currentUser!!.uid).setValue(user).addOnCompleteListener{
                    task -> if (task.isSuccessful){
                        Toast.makeText(this, "User has been registered!",
                        Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }
                    else {
                        Toast.makeText(
                            this, "Failed to register! Please Try Again",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                }
                /*FirebaseAuth.getInstance().currentUser?.let {
                    FirebaseDatabase.getInstance().getReference("Users").child(
                        it.uid
                    ).setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
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
                }*/
            }
            else {
                Toast.makeText(this, "Failed to register! Please Try Again",
                    Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }

        }

    }

}