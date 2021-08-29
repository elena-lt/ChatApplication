package com.example.data.sessionManager

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quickblox.auth.session.QBSession
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSessionParameters
import javax.inject.Inject

class SessionManger @Inject constructor(
    private val qbSessionManager: QBSessionManager,
    private val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor
) {

    private val _currUser = MutableLiveData<String>()
    val currUser: LiveData<String> = _currUser

    companion object {
        const val SP_USER_LOGIN = ""
        const val SP_ACCESS_TOKEN = ""
            }

    val listener = SharedPreferences.OnSharedPreferenceChangeListener{sharedPreferences, key ->
        when (key) {
            SP_USER_LOGIN -> {
                _currUser.value = sharedPreferences.getString(SP_USER_LOGIN, null)
            }
        }
    }

    fun login(userLogin: String) {
        sharedPreferencesEditor.putString(SP_USER_LOGIN, userLogin).apply()
    }

    fun logout() {
        sharedPreferencesEditor.putString(SP_USER_LOGIN, null).apply()
        sharedPreferencesEditor.putString(SP_ACCESS_TOKEN, null).apply()
    }

    fun registerListener(){
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregister(){
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun createSessionManagerListener() {
        qbSessionManager.addListener(object : QBSessionManager.QBSessionListener {
            override fun onSessionCreated(session: QBSession) {
                // calls when session was created firstly or after it has been expired
                Log.d("SESSION_MANAGER", "onSessionCreated: ")
            }

            override fun onSessionUpdated(sessionParameters: QBSessionParameters) {
                // calls when user signed in or signed up
                // QBSessionParameters stores information about signed in user.
                val userLogin = sessionParameters.userLogin
                val accessToken = sessionParameters.accessToken
                login(userLogin)

                Log.d(
                    "SESSION_MANAGER",
                    "onSessionUpdated: ${sessionParameters.accessToken}, ${sessionParameters.userLogin}"
                )
            }

            override fun onSessionDeleted() {
                // calls when user signed Out or session was deleted
                val sessionParameters = qbSessionManager.sessionParameters
                logout()

            }

            override fun onSessionRestored(session: QBSession) {
                // calls when session was restored from local storage
                Log.d(
                    "SESSION_MANAGER",
                    "onSessionRestored: ${session.token}, ${qbSessionManager.sessionParameters.userLogin}"
                )
                login( session.token)
            }

            override fun onSessionExpired() {
                // calls when session is expired
                Log.d("SESSION_MANAGER", "onSessionExpired: ")
            }

            override fun onProviderSessionExpired(provider: String) {
                // calls when provider's access token is expired or invalid
                Log.d("SESSION_MANAGER", "onProviderSessionExpired: ")
            }
        })
    }
}