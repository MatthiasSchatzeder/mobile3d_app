package com.mobile3d.application

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {



    override fun onNewToken(token: String) {
        Log.e("firebase", token)

        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {

        Log.e("firebase", "from: " + message.from)

        if(message.data.isNotEmpty()){
            Log.e("firebase" , "data: " + message.data)
        }

        /**
         * notification is not null
         */
        message.notification?.let {
            Log.e("firebase", "notification: " + it.title.toString() + " " + it.body.toString())

            //showNotification(it.title, it.body)
        }

        super.onMessageReceived(message)
    }   //onMessageReceived

    private fun showNotification(title: String?, body: String?) {

        val myNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("mobile_3d_channel", "Mobile3D", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "The Mobile 3D channel for push notifications"
            channel.lockscreenVisibility = 1
            myNotificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val myNotificationBuilder = NotificationCompat.Builder(applicationContext, "mobile_3d_channel_id")
            .setSmallIcon(R.drawable.ic_mobile_3d)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 0, 0, 0))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        myNotificationManager.notify(999, myNotificationBuilder.build())
    }
}