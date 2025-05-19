package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //    Test update xem cos dduoc khong 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.test).setOnClickListener {
            startActivity(Intent(this, Example::class.java))
        }


//        val test = findViewById<AnimatedCanvasView>(R.id.test)
//        var a= false
//        test.setOnClickListener {
//            if(a)
//                test.stopAnimations()
//            else
//                test.startAnimations()
//
//            a= !a
//        }

    }
}