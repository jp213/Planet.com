package com.example.planet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URI

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
            val image = intent.getByteArrayExtra("image")

            if (image != null && name != null) {
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                textView.text = name
                imageView.setImageBitmap(bitmap)
            }
        }

    }
}