package com.example.screenshot

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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ScreenshotActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private var storage = Firebase.storage

    // Reference to Firebase storage
    private var storageRef = storage.reference

    // Screenshot
    private lateinit var bitmap : Bitmap

    // Button pressed to take screenshot
    private lateinit var screenshotButton : ImageButton

    private lateinit var drawerLayout : DrawerLayout

    private lateinit var navigationView : NavigationView

    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle

    private lateinit var builder : AlertDialog.Builder

    private lateinit var name : String

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
                saveScreenshot(bitmap)
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
        var imagesRef = storageRef.child("images/" + name)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener() {
            println("Upload failed")
        }.addOnSuccessListener {
            println("Upload successful")
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
            R.id.nav_locations -> launchLocationGallery()
            R.id.nav_logout -> logout()
        }

        var drawer = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchScreenshotGallery() {
        val i = Intent(this, ScreenshotGallery::class.java)
        startActivity(i)
    }

    private fun launchLocationGallery() {
        val i = Intent(this, LocationGallery::class.java)
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