package com.example.chatapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityMainBinding
import com.example.chatapplication.utils.Constants.TAG
import com.example.core.utils.DataState
import com.example.data.sessionManager.SessionManger
import com.example.data.utils.ConnectivityManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnDataStateChangeListener {

    @Inject
    lateinit var sessionManager: SessionManger

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavGraph()

        Log.d(TAG, "onCreate: ${sessionManager.currUser.value}")
        initSessionManager()
        subscribeToObservers()

    }

    override fun onResume() {
        sessionManager.registerListener()
        sessionManager.createSessionManagerListener()
        super.onResume()
    }

    override fun onPause() {
        sessionManager.unregister()
        sessionManager.removeSessionManagerListener()
        super.onPause()
    }

    @ExperimentalCoroutinesApi
    private fun subscribeToObservers() {
        sessionManager.currUser.observe(this, { userLogin ->
            Log.d(TAG, "subscribeToObservers: new value collected $userLogin")
            if (userLogin != null && userLogin.isNotBlank()) {
                navigateToChatsFragment()
            } else {
                navigateToLoginFragment()
            }
        })

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                connectivityManager.networkStatus.collect {
                  when (it){
                      is ConnectivityManager.NetworkStatus.Available -> {
                          binding.tvNetworkStatus.visibility = View.GONE
                      }
                      is ConnectivityManager.NetworkStatus.Unavailable -> {
                          binding.tvNetworkStatus.visibility = View.VISIBLE
                      }
                  }
                }
            }
        }
    }

    private fun initSessionManager() {
//        sessionManager.registerListener()
//        sessionManager.createSessionManagerListener()
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
            NavOptions.Builder().setPopUpTo(R.id.chats_graph, inclusive = true, saveState = false).build()
        )
    }

    private fun setupNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onDataStateChanged(dataState: DataState<*>) {
        dataState.loading.let {
            Log.d(TAG, "onDataStateChanged: loading $it")
           binding.mainProgressBar.isVisible = it
        }

        dataState.errorMessage?.let {
            Log.d(TAG, "error..... $it")
        }

    }
}