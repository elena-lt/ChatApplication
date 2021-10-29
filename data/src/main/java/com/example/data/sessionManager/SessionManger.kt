package com.example.data.sessionManager

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.data.qbUtils.QbChatServiceUtils
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
    private val chatServiceUtils: QbChatServiceUtils,
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
                createChatService(qbSessionManager.sessionParameters.userLogin)
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

    fun loginToChatService(user: QBUser) {
        chatServiceUtils.login(user)
    }

    fun createChatService(userLogin: String) {
        val id = sharedPreferences.getInt(SP_USER_ID, -1)
        val data = sharedPreferences.getString(SP_DATA, "")
        val iv = sharedPreferences.getString(SP_IV, "")

        val pd = keyManager.decryptData(
            id.toString(),
            Base64.decode(data, Base64.DEFAULT),
            Base64.decode(iv, Base64.DEFAULT)
        )
        chatServiceUtils.login(userLogin, pd)
    }

    fun logoutFromChatService() {
        chatServiceUtils.logout()
    }

    fun removeSessionManagerListener() {
        qbSessionManager.removeListeners()
    }

}