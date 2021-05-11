package com.example.remort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class input : AppCompatActivity() {

    companion object {
        lateinit var ip_address : String
    }

    private var backpressedtime=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        supportActionBar?.hide()
        val bt:Button=findViewById(R.id.button)
        val ip_input=findViewById<TextInputEditText>(R.id.EditText)
        bt.setOnClickListener {
            if (ip_input.text?.isNotEmpty() == true) {
                val ip = ip_input.text.toString()
                val intent = Intent(this@input, MainActivity::class.java)
                intent.putExtra("ip_address", ip)
                ip_address = ip
                startActivity(intent)
                //finish()
            }else{
                Toast.makeText(applicationContext,"Enter ip Address",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBackPressed() {
        if(backpressedtime+2000> System.currentTimeMillis()) {
            super.onBackPressed()
        }else{
            Toast.makeText(applicationContext, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }
        backpressedtime= System.currentTimeMillis()
    }
}