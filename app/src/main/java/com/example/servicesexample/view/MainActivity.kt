package com.example.servicesexample.view

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.servicesexample.R
import com.example.servicesexample.service.MyPlayerService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var myPlayerService: MyPlayerService? = null

    val serviceConnection = object : ServiceConnection{


        override fun onServiceDisconnected(componenetName: ComponentName?) {

        }

        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            Log.d("TAG_X", "CONNECT SERVICE")
            myPlayerService = (iBinder as MyPlayerService.MyPlayerBinder).getPlayerService()
        }

    }

    val handler = Handler()

    private val myReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("my_key")?.let{mediaStatus ->
                handler.post{
                    text_view_status.text = mediaStatus
                }
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, MyPlayerService::class.java)
        registerReceiver(myReceiver, IntentFilter("com.example.serviceexampe.service.play"))

        button_play.setOnClickListener{
            myPlayerService?.onPlayPlayer()?:{
                Log.d("TAG_X", "START SERVICE")
                //startService(intent)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }()
        }

        button_pause.setOnClickListener{
            myPlayerService?.onPausePlayer()
        }

        button_stop.setOnClickListener{
            //myPlayerService?.onStopPlayer()

            //stopService(intent)
            if(myPlayerService != null) {
                unbindService(serviceConnection)
                myPlayerService = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG_X", "onDestroy")
        unbindService(serviceConnection)
        unregisterReceiver(myReceiver)
    }
}
