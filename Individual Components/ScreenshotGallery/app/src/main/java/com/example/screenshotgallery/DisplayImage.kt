package com.example.screenshotgallery

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisplayImage : AppCompatActivity() {

    private lateinit var imageView : ImageView

    private lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        imageView = findViewById(R.id.my_image_view)
        textView = findViewById(R.id.image_name)

        val intent = intent

        if (intent.extras != null ) {
            val name = intent.getStringExtra("name")
            val image = intent.getIntExtra("image", 0)

            textView.text = name
            imageView.setImageResource(image)
        }

    }
}