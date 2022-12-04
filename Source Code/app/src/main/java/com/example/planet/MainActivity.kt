package com.example.planet

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var start : Button
    private lateinit var banner : TextView
    private lateinit var video : VideoView
    private var stop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start = findViewById(R.id.getStarted)
        banner = findViewById(R.id.banner)
        video = findViewById(R.id.videoView)
        start.setOnClickListener(this)
        banner.setOnClickListener(this)
        val path = "android.resource://" + packageName + "/" + R.raw.globe
        video.setVideoURI(Uri.parse(path))
        video.start()
        if (stop) {
            video.stopPlayback()
        }

    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.banner -> Toast.makeText(this, "Please click get started!", Toast.LENGTH_LONG).show()
            R.id.getStarted ->  launchGetStartedActivity()
        }
    }



    private fun launchGetStartedActivity() {
        val getStartedIntent = Intent(this, LoginActivity::class.java)
        startActivity(getStartedIntent)
    }
}