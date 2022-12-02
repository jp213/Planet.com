package com.example.screenshotgallery

import android.content.Context
import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    private lateinit var gridView : GridView

    private var names = arrayOf("Test")

    private var images = arrayOf(R.drawable.ic_launcher_background)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.my_grid_view)
        var customAdaptor = CustomAdapter(names, images, this)
        gridView.adapter = customAdaptor
        gridView.onItemClickListener = AdapterView.OnItemClickListener() {
                adapterView, view, i, l ->
            val name = names[i]
            val image = images[i]
            val intent = Intent(this, DisplayImage::class.java)
            startActivity(intent.putExtra("name", name).putExtra("image", image))
        }
    }

    class CustomAdapter : BaseAdapter {
        private lateinit var imageNames : Array<String>
        private lateinit var images : Array<Int>
        private lateinit var context : Context
        private lateinit var layoutInflater : LayoutInflater

        constructor(imageNames: Array<String>, images: Array<Int>, context: Context) : super() {
            this.imageNames = imageNames
            this.images = images
            this.context = context
            layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }


        override fun getCount(): Int {
            return images.size
        }

        override fun getItem(p0: Int): Any? {
            return null
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
            photo?.setImageResource(images[p0])

            return view
        }

    }
}