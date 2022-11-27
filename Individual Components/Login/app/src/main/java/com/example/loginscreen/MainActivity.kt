package com.example.loginscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var register :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register = findViewById(R.id.register)
        register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                when (view.id) {
                    R.id.register -> launchRegisterActivity()
                }

            }
        })
    }
    private fun launchRegisterActivity(){
        val registerUserIntent = Intent(this,RegisterUser::class.java)
        startActivity(registerUserIntent)
    }

}




