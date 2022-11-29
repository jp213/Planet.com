package com.example.welcomescreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var start : Button
    private lateinit var banner : TextView
    private var progressStatus = 0
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start = findViewById(R.id.getStarted)
        progressBar = findViewById(R.id.progressBarHorizontal)
        banner = findViewById(R.id.banner)
        start.setOnClickListener(this)
        banner.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.banner -> Toast.makeText(this, "Please click get started!", Toast.LENGTH_LONG).show()
            R.id.getStarted -> {
                progressBar.visibility = View.VISIBLE
                Thread(Runnable {
                    while (progressStatus < 100) {
                        progressStatus += 1

                        try {
                            Thread.sleep(100)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        handler.post{
                            progressBar.progress = progressStatus
                        }
                        if (progressStatus == 100){
                            launchGetStartedActivity()
                        }
                    }
                }).start()
            }
        }
    }



    private fun launchGetStartedActivity() {
        val getStartedIntent = Intent(this, ProfileActivity::class.java)
        startActivity(getStartedIntent)
    }
}