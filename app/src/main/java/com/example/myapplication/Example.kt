package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap

class Example : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var nameEditText: EditText
    private lateinit var valueEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var deleteAllButton: Button
    private lateinit var adapter: TableNameDataAdapter
    private lateinit var dataList: ArrayList<LocalSQLite.TableNameData>

    private fun updateListView() {
        // Fetch updated data from SQLite
        dataList = LocalSQLite.DB(this).get()
        adapter = TableNameDataAdapter(this, dataList)
        listView.adapter = adapter
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)


        // Initialize Views
        listView = findViewById(R.id.listView)
        nameEditText = findViewById(R.id.nameEditText)
        valueEditText = findViewById(R.id.valueEditText)
        imageView = findViewById(R.id.imageView)
        saveButton = findViewById(R.id.saveButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        deleteAllButton = findViewById(R.id.deleteAllButton)

        // Initialize data list and adapter
        dataList = LocalSQLite.DB(this).get() // Fetch all data from SQLite
        adapter = TableNameDataAdapter(this, dataList)
        listView.adapter = adapter

        // Set click listener for Save button
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val value = valueEditText.text.toString().toIntOrNull() ?: 0
            val bitmap = imageView.drawable.toBitmap() // Convert ImageView to Bitmap
            val tableData = LocalSQLite.TableNameData(name, value, bitmap)

            // Save the new data to SQLite
                //LocalSQLite.DB(this).save(tableData)

            // Update the ListView
            updateListView()
        }

        // Set click listener for Update button
        updateButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val value = valueEditText.text.toString().toIntOrNull() ?: 0
            val bitmap = imageView.drawable.toBitmap() // Convert ImageView to Bitmap
            val tableData = LocalSQLite.TableNameData(name, value, bitmap)

            // Update the data in SQLite
            LocalSQLite.DB(this).update(tableData)

            // Update the ListView
            updateListView()
        }

        // Set click listener for Delete button
        deleteButton.setOnClickListener {
            val name = nameEditText.text.toString()

            // Delete the data from SQLite
            LocalSQLite.DB(this).delete(name)

            // Update the ListView
            updateListView()
        }

        // Set click listener for Delete All button
        deleteAllButton.setOnClickListener {
            // Delete all data from SQLite
            LocalSQLite.DB(this).delete()

            // Update the ListView
            updateListView()
        }
    }

    //end activity
}