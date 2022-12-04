package com.example.screenshot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class ScreenshotGallery : AppCompatActivity() {
    private lateinit var gridView : GridView

    // Instance of Firebase storage
    private var storage = Firebase.storage

    private var auth = Firebase.auth

    // Reference to Firebase storage
    private var storageRef = storage.reference

    private var names = ArrayList<String>()
    private var images = ArrayList<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screenshot_gallery)

        retrieveImages()
    }

    private fun createGallery() {
        gridView = findViewById(R.id.my_grid_view)
        var customAdaptor = CustomAdapter(names, images, this)
        gridView.adapter = customAdaptor
        gridView.onItemClickListener = AdapterView.OnItemClickListener() {
                adapterView, view, i, l ->
            val name = names[i]
            val image = images[i]

            val bStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, bStream)
            val arr = bStream.toByteArray()

            val i = Intent(this, DisplayImage::class.java)
            startActivity(i.putExtra("name", name).putExtra("image", arr))
        }
    }

    private fun retrieveFilenames() {
        var counter = 0
        var currUser = auth.currentUser?.uid
        var imageRef = storageRef.child("user/" + currUser.toString())
        imageRef.listAll().addOnSuccessListener { item ->
            for (file in item.items) {
                file.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    println(url)
                    val regex = "%2F(\\w+)?".toRegex()
                    val filename = regex.findAll(url, 0).elementAt(1)?.value?.substring(3)

                    if (filename != null) {
                        names.add(filename)
                    }

                    counter++

                    if (counter == item.items.size) {
                        createGallery()
                    }
                }
            }
        }
    }

    private fun retrieveImages() {
        var counter = 0
        var currUser = auth.currentUser?.uid
        var imageRef = storageRef.child("user/" + currUser.toString())
        imageRef.listAll().addOnSuccessListener { item ->
            for (file in item.items) {
                file.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    images.add(bitmap)
                    counter++

                    if (counter == item.items.size) {
                        retrieveFilenames()
                    }
                }
            }
        }
    }



    class CustomAdapter : BaseAdapter {
        private lateinit var imageNames: ArrayList<String>
        private lateinit var images: ArrayList<Bitmap>
        private lateinit var context: Context
        private lateinit var layoutInflater: LayoutInflater

        constructor(imageNames: ArrayList<String>, images: ArrayList<Bitmap>, context: Context) : super() {
            if (imageNames != null) {
                this.imageNames = imageNames
            }
            if (images != null) {
                this.images = images
            }
            this.context = context
            layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }


        override fun getCount(): Int {
            return images.size
        }

        override fun getItem(p0: Int): Any? {
            return images[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
            var view = p1
            if (view == null) {
                view = layoutInflater.inflate(R.layout.row_items, p2, false)
            }

            var name = view?.findViewById<TextView>(R.id.image_name)
            var photo = view?.findViewById<ImageView>(R.id.my_image_view)

            name?.text = imageNames[p0]
            photo?.setImageBitmap(images[p0])

            return view
        }
    }
}