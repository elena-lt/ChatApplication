package com.example.data.qbUtils

import android.os.Bundle
import android.util.Log
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.*
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import java.lang.Exception
import javax.inject.Inject

class QbChatServiceUtils @Inject constructor(
    private val qbChatService: QBChatService
) {

    private val connectionListener = object : ConnectionListener {
        override fun connected(p0: XMPPConnection?) {
            Log.d("ChatService", "connected: ")
        }

        override fun authenticated(p0: XMPPConnection?, p1: Boolean) {
            Log.d("ChatService", "authenticated: ")
        }

        override fun connectionClosed() {
            Log.d("ChatService", "connectionClosed: ")
        }

        override fun connectionClosedOnError(p0: Exception?) {
            Log.d("ChatService", "connectionClosedOnError: ")
        }

        override fun reconnectionSuccessful() {
            Log.d("ChatService", "reconnectionSuccessful: ")
        }

        override fun reconnectingIn(p0: Int) {
            Log.d("ChatService", "reconnectingIn: ")
        }

        override fun reconnectionFailed(p0: Exception?) {
            Log.d("ChatService", "reconnectionFailed: ")
        }
    }

    init {
        registerSessionManagerListener()
    }

    private fun registerSessionManagerListener() {
        qbChatService.addConnectionListener(connectionListener)
    }

    fun login(user: QBUser) {
        if (qbChatService.user == null) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    qbChatService.setUseStreamManagement(true)
                    qbChatService.login(user, object : QBEntityCallback<Void> {
                        override fun onSuccess(p0: Void?, p1: Bundle?) {
                            registerSessionManagerListener()
                        }

                        override fun onError(p0: QBResponseException?) {
                            Log.d("AppDebug", "onError: chat service not created ${p0?.message}")
                        }
                    })
                }
            }
        }

    }

    fun login(userLogin: String, userPassword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = QBUser(userLogin, userPassword)
            kotlin.runCatching {
                QBUsers.signIn(user).perform()
            }.onSuccess {
                login(user)
            }.onFailure {
                Log.d("AppDebug", "onError: USER NOT LOGGED IN -> ${it}")
            }
        }
    }

    fun logout() {
        qbChatService.logout(object : QBEntityCallback<Void> {
            override fun onSuccess(p0: Void?, p1: Bundle?) {
                Log.d("AppDebug", "onSuccess: logout")
            }

            override fun onError(p0: QBResponseException?) {
                Log.d("AppDebug", "onError: ${p0?.message}")
            }
        })
    }
}