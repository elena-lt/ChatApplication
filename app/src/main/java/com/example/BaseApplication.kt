package com.example

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatapplication.BuildConfig
import com.example.chatapplication.BuildConfig.*
import com.example.chatapplication.R
import com.example.chatapplication.utils.showNotification
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.core.LogLevel
import com.quickblox.messages.QBPushNotifications
import com.quickblox.messages.services.QBPushManager
import dagger.hilt.android.HiltAndroidApp
import java.lang.Exception

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupQuickBlox()

        registerPushNotifications()
    }

    private fun setupQuickBlox() {
        QBSettings.getInstance()
            .init(applicationContext, QB_APPLICATION_ID, QB_AUTH_KEY, QB_AUTH_SECRET)
        QBSettings.getInstance().accountKey = BuildConfig.QB_ACCOUNT_KEY
        QBSettings.getInstance().logLevel = LogLevel.DEBUG
        QBSettings.getInstance().isEnablePushNotification = true
        QBChatService.setDebugEnabled(true)

        //Subscription status
        QBPushManager.getInstance().addListener(object : QBPushManager.QBSubscribeListener {
            override fun onSubscriptionCreated() {
                Log.d("PUSH_NOTIFICATION", "onSubscriptionCreated: ")
            }

            override fun onSubscriptionError(p0: Exception?, p1: Int) {
                Log.d("PUSH_NOTIFICATION", "onSubscriptionError: ${p0?.message} ")
            }

            override fun onSubscriptionDeleted(p0: Boolean) {
                Log.d("PUSH_NOTIFICATION", "onSubscriptionDeleted: ")
            }
        })
    }

    private fun registerPushNotifications() {
        val pushReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getStringExtra("message")
                val from = intent?.getStringExtra("from")
                showNotification(applicationContext, message!!, from ?: "Someone secret", 1)
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(pushReceiver, IntentFilter("new-push-event"))
    }
}