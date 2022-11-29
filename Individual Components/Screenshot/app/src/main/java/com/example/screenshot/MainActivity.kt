package com.example.screenshot

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Instance of Firebase storage
    private var storage = Firebase.storage

    // Reference to Firebase storage
    private var storageRef = storage.reference

    // Button pressed to take screenshot
    private lateinit var button : Button

    private lateinit var builder : AlertDialog.Builder

    private var name : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        builder = AlertDialog.Builder(this)
        button = findViewById(R.id.screenshot)

        button.setOnClickListener(this)
    }

    private fun getScreenshot() {
        val v: View = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        saveScreenshot(bitmap)
    }

    private fun getFilename() {
        builder.setTitle("Name Screenshot")
        val inflator = LayoutInflater.from(this)
        val dialogLayout = inflator.inflate(R.layout.popup, null)
        val userInput = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Ok") { dialog, i ->
            name = userInput.text.toString()
        }

        builder.setNegativeButton("Cancel") { dialog, i ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveScreenshot(bitmap : Bitmap) {
        getFilename()
        println(name)
        if (name != null) {
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
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.screenshot -> getScreenshot()
        }
    }
}