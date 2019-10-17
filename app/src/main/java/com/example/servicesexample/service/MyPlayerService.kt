package com.example.servicesexample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.example.servicesexample.R

class MyPlayerService : Service() {
    lateinit var mediaPlayer: MediaPlayer
    private val myBinder = MyPlayerBinder()
    lateinit var notificationManger: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        onPlayPlayer()
        return myBinder
    }

    private fun createNotification(): Notification {
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        val notificationChannel = NotificationChannel(
                "my_channel",
                "my player channel",
                NotificationManager.IMPORTANCE_LOW
            )

            notificationManger.createNotificationChannel(notificationChannel)

            notification = Notification.Builder(this, "my_channel")
                .setSmallIcon(R.drawable.ic_play)
                .setTicker("My Sound is playing")
                .build()
        } else {
            notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker("My Sound is playing")
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .build()
        }

        return notification
    }

    override fun onCreate() {
        super.onCreate()
        notificationManger = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManger.notify(1, createNotification())
        mediaPlayer = MediaPlayer.create(this, R.raw.e1m1)
        mediaPlayer.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //mediaPlayer.start()
        //onPlayPlayer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManger.cancel(1)
        sendBroadcast(Intent("com.example.serviceexampe.service.play").apply {
            this.putExtra("my_key", "STOP")
        })

        mediaPlayer.stop()
    }

    fun onPausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            sendBroadcast(Intent("com.example.serviceexampe.service.play").apply {
                this.putExtra("my_key", "PAUSED")
            })
        }
    }

    fun onPlayPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            sendBroadcast(Intent("com.example.serviceexampe.service.play").apply {
                this.putExtra("my_key", "PLAYING")
            })
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }
//    fun onStopPlayer(){
//        if(mediaPlayer.isPlaying)
//            mediaPlayer.stop()
//    }

    inner class MyPlayerBinder : Binder() {
        fun getPlayerService(): MyPlayerService {
            return this@MyPlayerService
        }
    }
}