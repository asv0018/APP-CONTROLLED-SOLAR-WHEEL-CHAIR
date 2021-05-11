package com.example.remort

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.remort.input.Companion.ip_address
import android.os.Vibrator as Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.Socket
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var active = false
    private var address_of_controller = "192.168.43.106"
    private var port_of_controller = 1337
    var data_to_transmit = ""

    lateinit var writer: Socket
    lateinit var connect:Socket
    lateinit var transmit_data:JSONObject


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

       address_of_controller = ip_address
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        active = true
        transmit_data = JSONObject()
        transmit_data.put("f",0)
        transmit_data.put("b",0)
        transmit_data.put("l",0)
        transmit_data.put("r",0)
        transmit_data.put("e",0)


            CoroutineScope(IO).launch{
                try{
                connect = client_connect(address_of_controller, port_of_controller)
                if(connect.isConnected){

                    client_receive_data(connect)
                }

                }catch (e:Exception){
                    Log.getStackTraceString(e)
                    Log.d("MANOHAR", e.toString())
                }
            }



        val top: ImageButton = findViewById(R.id.top)
        val bottom: ImageButton = findViewById(R.id.bottom)
        val right: ImageButton = findViewById(R.id.right)
        val left: ImageButton = findViewById(R.id.left)
        val center: ImageButton =findViewById(R.id.center)
        val text: TextView= findViewById(R.id.text)

        top.run {

            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            vibration()
                            beep()
                            transmit_data.put("f",1)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                            
                        }
                        MotionEvent.ACTION_UP -> {
                            vibration()
                            transmit_data.put("f",0)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        }

        bottom.run {

            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            vibration()
                            beep()
                            transmit_data.put("b",1)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            vibration()
                            transmit_data.put("b",0)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        }

        right.run {

            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            vibration()
                            beep()
                            transmit_data.put("r",1)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            vibration()
                            transmit_data.put("r",0)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        }

        left.run {

            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            vibration()
                            beep()
                            transmit_data.put("l",1)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            vibration()
                            transmit_data.put("l",0)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        }

        center.run {

            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            vibration()
                            beep()
                            transmit_data.put("e",1)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            vibration()
                            transmit_data.put("e",0)
                            CoroutineScope(IO).launch {
                            client_write(connect, transmit_data.toString()+"\n")
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun vibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100)
        }
    }
    private fun beep(){
         val mp = MediaPlayer.create(this,R.raw.beep)
        mp.start()
    }





    private suspend fun client_connect(address:String, port:Int): Socket {
        val connection = Socket(address,port)

        return connection
    }

    private suspend fun client_receive_data(
        connector: Socket
    ){
        val reader = Scanner(connector.getInputStream())
        while(active){
                if(reader.hasNextLine()){
                    val data = reader.nextLine()
                    Log.d("ESP8266",  data)
                    try{
                        reader.nextLine()
                    }catch (e:Exception){
                        Log.d("TAG",e.toString())
                    }

                }

        }
        reader.close()

    }

    private suspend fun client_write(connector: Socket, data:String){
            val writer = connector.getOutputStream()
        Log.d("CHECK", data.toString())
        Log.d("CHECK",data.toByteArray().toString())
            val byteArray = data.toByteArray()
            writer.write(byteArray)
    }


    private var backpressedtime=0L
    override fun onBackPressed() {
        if(backpressedtime+2000> System.currentTimeMillis()) {
            super.onBackPressed()
        }else{
            Toast.makeText(applicationContext, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }
        backpressedtime= System.currentTimeMillis()
    }

}

