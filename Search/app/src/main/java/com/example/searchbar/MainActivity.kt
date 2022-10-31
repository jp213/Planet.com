package com.example.searchbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<*>
    private lateinit var listView : ListView  // Create views which can be used by adapter
    private lateinit var emptyView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create an adaptable list array to show countries
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.countries_array))

        listView = findViewById(R.id.lv_listView)
        listView.adapter = adapter // set adapter in a list format
        listView.onItemClickListener = AdapterView.OnItemClickListener {parent, view, position, id ->
            Toast.makeText(applicationContext, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        } //set an onitemclick listener

        emptyView = findViewById((R.id.tv_emptyTextView)) //show an empty view if nothing is clicked
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)

        // Item to be found when something is searched
        val search : MenuItem? = menu?.findItem((R.id.nav_search))

        // await the action of a search. if nothing is searched the hint will appear
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search for Something"

        // if nothing was submitted we return false
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            // if text changed, we update the adapter to contain the next text
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)

    }
}