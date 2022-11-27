package com.example.loginscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var email : EditText
    private lateinit var passwordButton : Button
    private lateinit var progressBar: ProgressBar
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        email = findViewById(R.id.email)
        passwordButton = findViewById(R.id.resetPassword)
        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()

        passwordButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        resetPassword()
    }

    private fun resetPassword() {
        val emailString = email.text.toString().trim()
        if(emailString.isEmpty()){
            email.error = "Email is required!"
            email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = "Please provide correct email associated with account"
            email.requestFocus()
            return
        }
        progressBar.visibility = View.VISIBLE
        auth.sendPasswordResetEmail(emailString).addOnCompleteListener {task ->
            if(task.isSuccessful) {
                Toast.makeText(this, "A password reset link was set to your email.",
                    Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Something wrong happened.",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}