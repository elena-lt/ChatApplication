package com.example.chatapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.chatapplication.BuildConfig.*
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityMainBinding

import com.example.chatapplication.utils.Constants.TAG
import com.example.core.utils.DataState
import com.example.data.sessionManager.SessionManger
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.core.LogLevel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnDataStateChangeListener {

    @Inject
    lateinit var sessionManager: SessionManger

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavGraph()

        Log.d(TAG, "onCreate: ${sessionManager.currUser.value}")

        initSessionManager()
        setupQuickBlox()
        subscribeToObservers()

    }

    override fun onPause() {
        sessionManager.unregister()
        super.onPause()
    }

    private fun subscribeToObservers() {
        sessionManager.currUser.observe(this, { userLogin ->
            if (userLogin != null && userLogin.isNotBlank()) {
                navigateToChatsFragment()
            } else {
                navigateToLoginFragment()
            }
        })
    }

    private fun setupQuickBlox() {
        QBSettings.getInstance()
            .init(applicationContext, QB_APPLICATION_ID, QB_AUTH_KEY, QB_AUTH_SECRET)
        QBSettings.getInstance().accountKey = QB_ACCOUNT_KEY
        QBSettings.getInstance().logLevel = LogLevel.DEBUG
        QBChatService.setDebugEnabled(true)
    }

    private fun initSessionManager() {
        sessionManager.registerListener()
        sessionManager.createSessionManagerListener()
    }

    private fun navigateToLoginFragment() {
        navController.navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
        )
    }

    private fun navigateToChatsFragment() {
        navController.navigate(
            R.id.chats_graph,
            null,
            NavOptions.Builder().setPopUpTo(R.id.chats_graph, true, false).build()
        )
    }

    private fun setupNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onDataStateChanged(dataState: DataState<*>) {
        dataState.loading.let {
            Log.d(TAG, "loading.....")
        }

        dataState.errorMessage?.let {
            Log.d(TAG, "error..... $it")
        }

    }
}