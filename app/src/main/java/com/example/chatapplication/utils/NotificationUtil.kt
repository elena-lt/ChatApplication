package com.example.chatapplication.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.chatapplication.R
import com.example.chatapplication.utils.Constants.CHANNEL_ONE_ID


fun showNotification(context: Context, message: String, from: String, notificationId: Int) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        if (nm.getNotificationChannel(CHANNEL_ONE_ID) == null) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                Constants.CHANNEL_ONE_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            nm.createNotificationChannel(notificationChannel)
        }
    }else{
        val nc = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
            .setContentTitle(from)
            .setContentText(message)
            .setSmallIcon(R.drawable.new_chat)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(notificationId, nc)
    }
}