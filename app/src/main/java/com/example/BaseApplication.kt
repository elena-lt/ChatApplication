package com.example

import android.app.Application
import com.example.chatapplication.BuildConfig
import com.example.chatapplication.BuildConfig.*
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.core.LogLevel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupQuickBlox()
    }
    private fun setupQuickBlox() {
        QBSettings.getInstance()
            .init(applicationContext, QB_APPLICATION_ID, QB_AUTH_KEY, QB_AUTH_SECRET)
        QBSettings.getInstance().accountKey = BuildConfig.QB_ACCOUNT_KEY
        QBSettings.getInstance().logLevel = LogLevel.DEBUG
        QBChatService.setDebugEnabled(true)
    }
}