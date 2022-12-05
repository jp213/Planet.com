package com.example.planet

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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

class ScreenshotActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private var storage = Firebase.storage

    // Reference to Firebase storage
    private var storageRef = storage.reference

    private var auth = Firebase.auth

    private var currUser = auth.currentUser!!.uid

    private var database = Firebase.database

    private var databaseRef = database.getReference("Users")

    // Screenshot
    private lateinit var bitmap : Bitmap

    // Button pressed to take screenshot
    private lateinit var screenshotButton : ImageButton

    private lateinit var drawerLayout : DrawerLayout

    private lateinit var navigationView : NavigationView

    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle

    private lateinit var builder : AlertDialog.Builder

    private lateinit var name : String

    private lateinit var names : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screenshot)

        builder = AlertDialog.Builder(this)
        screenshotButton = findViewById(R.id.screenshot)
        drawerLayout = findViewById(R.id.my_drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        navigationView.setNavigationItemSelectedListener(this)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        screenshotButton.setOnClickListener(this)

        databaseRef.child(currUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Use empty constructor to get name and email from Firebase
                val userProfile = snapshot.getValue(User::class.java)
                if (userProfile != null) {
                    names = userProfile.screenshots as ArrayList<String>
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // If the data could not be pulled
                Toast.makeText(this@ScreenshotActivity, "Uh oh, an error occurred", Toast.LENGTH_LONG)
                    .show()
            }
        })


    }

    private fun getFilename() {
        builder.setTitle("Name Screenshot")
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.popup, null)
        val userInput = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Ok") { dialog, i ->
            dialog.dismiss()
            name = userInput.text.toString()
            if (name != "") {
                names.add(name)

                database.getReference("Users/$currUser").updateChildren(hashMapOf("screenshots" to names) as Map<String, Any>).addOnSuccessListener {
                    saveScreenshot(bitmap)
                }
            } else if (names.contains(name)) {
                Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
                getFilename()
            } else {
                Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show()
                getFilename()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, i ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun getScreenshot() {
        val v: View = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        this.bitmap = bitmap

        getFilename()
    }

    private fun saveScreenshot(bitmap : Bitmap) {
        var imagesRef = storageRef.child("user/" + currUser.toString() + "/" + name)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener() {
            Toast.makeText(this, "Screenshot failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Screenshot saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.screenshot -> getScreenshot()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_profile -> launchProfileActivity()
            R.id.nav_screenshots -> launchScreenshotGallery()
            R.id.nav_logout -> logout()
        }

        var drawer = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchScreenshotGallery() {
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

}