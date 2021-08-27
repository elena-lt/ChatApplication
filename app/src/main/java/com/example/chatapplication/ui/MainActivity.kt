package com.example.chatapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapplication.BuildConfig.*
import com.example.chatapplication.databinding.ActivityMainBinding
import com.example.chatapplication.utils.Constants.TAG
import com.example.core.utils.DataState
import com.quickblox.auth.session.QBSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnDataStateChangeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupQuickBlox()

    }

    private fun setupQuickBlox(){
        QBSettings.getInstance().init(applicationContext, QB_APPLICATION_ID, QB_AUTH_KEY, QB_AUTH_SECRET)
        QBSettings.getInstance().accountKey = QB_ACCOUNT_KEY
    }

    override fun onDataStateChanged(dataState: DataState<*>) {
        dataState.loading.let{
            Log.d(TAG, "loading.....")
        }

        dataState.errorMessage?.let{
            Log.d(TAG, "error..... $it")
        }

    }
}