package com.example.planet

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class SaveActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var gridView : GridView

    // Instance of Firebase storage
    private var storage = Firebase.storage

    private var auth = Firebase.auth

    private var database = Firebase.database

    private var databaseRef = database.getReference("Users")

    var currUser = auth.currentUser!!.uid

    // Reference to Firebase storage
    private var storageRef = storage.reference

    private var images = ArrayList<Bitmap?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        retrieveFilenames(object : MyCallback {
            override fun onCallback(list: ArrayList<String>) {
                retrieveImages(list)
            }
        })
    }

    private fun retrieveFilenames(callback : MyCallback) {
        databaseRef.child(currUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Use empty constructor to get name and email from Firebase
                val userProfile = snapshot.getValue(User::class.java)
                if (userProfile != null) {
                    var names = userProfile.screenshots as ArrayList<String>

                    for (i in 0..names.size-1) {
                        images.add(null)
                    }

                    callback.onCallback(names)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // If the data could not be pulled
                Toast.makeText(this@SaveActivity, "Uh oh, an error occurred", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun createGallery(names : ArrayList<String>) {
        gridView = findViewById(R.id.my_grid_view)
        val customAdaptor = CustomAdapter(names, images, this)
        gridView.adapter = customAdaptor
        gridView.onItemClickListener = AdapterView.OnItemClickListener() {
                adapterView, view, i, l ->
            val name = names[i]
            val image = images[i]

            val bStream = ByteArrayOutputStream()
            if (image != null) {
                image.compress(Bitmap.CompressFormat.JPEG, 100, bStream)
            }
            val arr = bStream.toByteArray()

            val i = Intent(this, DisplayImage::class.java)
            startActivity(i.putExtra("name", name).putExtra("image", arr))
        }
    }


    private fun retrieveImages(filenames : ArrayList<String>) {
        var counter = 0
        var imageRef = storageRef.child("user/$currUser")
        imageRef.listAll().addOnSuccessListener { item ->
            for (file in item.items) {
                file.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val filename = file.name
                    val insertIndex = filenames.indexOf(filename)
                    println(insertIndex)
                    images.set(insertIndex, bitmap)
                    println(images)
                    counter++

                    if (counter == item.items.size) {
                        createGallery(filenames)
                    }
                }
            }
        }
    }

    class CustomAdapter : BaseAdapter {
        private lateinit var imageNames: ArrayList<String>
        private lateinit var images: ArrayList<Bitmap?>
        private lateinit var context: Context
        private lateinit var layoutInflater: LayoutInflater

        constructor(imageNames: ArrayList<String>, images: ArrayList<Bitmap?>, context: Context) : super() {
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

            val name = view?.findViewById<TextView>(R.id.image_name)
            val photo = view?.findViewById<ImageView>(R.id.my_image_view)

            name?.text = imageNames[p0]
            photo?.setImageBitmap(images[p0])
            return view
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> launchProfileActivity()
            R.id.nav_screenshots -> launchSaveActivity()
            R.id.nav_logout -> logout()
        }

        var drawer = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchSaveActivity() {
        val i = Intent(this, SaveActivity::class.java)
        startActivity(i)
    }

    /*
 This is used to create an intent which will change from the home screen to the profile
 screen upon successful login
  */
    private fun launchProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    private fun logout() {
        finishAffinity()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    interface MyCallback {
        fun onCallback(list : ArrayList<String>);
    }
}