package com.example.data.sessionManager

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.data.sessionManager.keyManager.KeyManager
import com.example.data.utils.Const.SP_DATA
import com.example.data.utils.Const.SP_IV
import com.example.data.utils.Const.SP_USER_ID
import com.example.data.utils.Const.SP_USER_LOGIN
import com.quickblox.auth.session.QBSession
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSessionParameters
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

@RequiresApi(Build.VERSION_CODES.M)
class SessionManger @Inject constructor(
    private val qbSessionManager: QBSessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor,
    private val qbChatService: QBChatService,
    private val keyManager: KeyManager
) {

    private val _currUser =
        MutableLiveData<String?>(sharedPreferences.getString(SP_USER_LOGIN, null))
    val currUser: LiveData<String?> = _currUser

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                SP_USER_LOGIN -> {
                    _currUser.postValue(sharedPreferences.getString(SP_USER_LOGIN, null))
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun login(userId: Int, userLogin: String) {
        val prevUserId = sharedPreferences.getInt(SP_USER_ID, -1)
        val prevUserLogin = sharedPreferences.getString(SP_USER_LOGIN, null)

//        sharedPreferencesEditor.putString(SP_USER_LOGIN, userLogin).apply()
//        sharedPreferencesEditor.putInt(SP_USER_ID, userId).apply()

        if (userId != prevUserId) {
            sharedPreferencesEditor.putInt(SP_USER_ID, userId).apply()
        }
        if (userLogin != prevUserLogin) {
            sharedPreferencesEditor.putString(SP_USER_LOGIN, userLogin).apply()
        }
    }

    fun logout() {
        sharedPreferencesEditor.putString(SP_USER_LOGIN, null).apply()
        sharedPreferencesEditor.putString(SP_USER_ID, null).apply()
    }

    fun registerListener() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregister() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun createSessionManagerListener() {
        qbSessionManager.addListener(object : QBSessionManager.QBSessionListener {
            override fun onSessionCreated(session: QBSession) {
                // calls when session was created firstly or after it has been expired
            }

            override fun onSessionUpdated(sessionParameters: QBSessionParameters) {
                // calls when user signed in or signed up
                // QBSessionParameters stores information about signed in user.
                login(sessionParameters.userId, sessionParameters.userLogin)

                Log.d("AppDebug", "SESSION UPDATED: ${sessionParameters.userLogin}")
            }

            override fun onSessionDeleted() {
                // calls when user signed Out or session was deleted
                Log.d("AppDebug", "SESSION DELETED")
                logout()
            }

            override fun onSessionRestored(session: QBSession) {
                // calls when session was restored from local storage
                login(
                    qbSessionManager.sessionParameters.userId,
                    qbSessionManager.sessionParameters.userLogin
                )
                createChatService(
                    qbSessionManager.sessionParameters.userLogin,
                    qbSessionManager.sessionParameters.userPassword
                )
                Log.d("AppDebug", "SESSION RESTORED ${session.userId}")
            }

            override fun onSessionExpired() {
                // calls when session is expired
                Log.d("AppDebug", "onSessionExpired: ")
            }

            override fun onProviderSessionExpired(provider: String) {
                // calls when provider's access token is expired or invalid
                Log.d("AppDebug", "onProviderSessionExpired: ")
            }
        })

    }

    fun createChatServiceWithUser(user: QBUser) {
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

    private fun registerSessionManagerListener() {
        val connectionListener = object : ConnectionListener {
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
        qbChatService.addConnectionListener(connectionListener)
    }

    fun createChatService(userLogin: String, userPassword: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                val id = sharedPreferences.getInt(SP_USER_ID, -1)
                val data = sharedPreferences.getString(SP_DATA, "")
                val iv = sharedPreferences.getString(SP_IV, "")

                val pd = keyManager.decryptData(
                    id.toString(),
                    Base64.decode(data, Base64.DEFAULT),
                    Base64.decode(iv, Base64.DEFAULT)
                )
                val user = QBUser(userLogin, pd)
                kotlin.runCatching {
                    QBUsers.signIn(user).perform()
                }.onSuccess {
                    createChatServiceWithUser(user)
                }.onFailure {
                    Log.d("AppDebug", "onError: USER NOT LOGGED IN -> ${it}")
                }
            }
        }
    }

    fun logoutFromChatService() {
        qbChatService.logout(object : QBEntityCallback<Void> {
            override fun onSuccess(p0: Void?, p1: Bundle?) {
                Log.d("AppDebug", "onSuccess: logout")
            }

            override fun onError(p0: QBResponseException?) {
                Log.d("AppDebug", "onError: ${p0?.message}")
            }
        })
    }

    fun removeSessionManagerListener() {
        qbSessionManager.removeListeners()
    }

}