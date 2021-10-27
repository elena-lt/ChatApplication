package com.example.chatapplication

import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.ui.OnDataStateChangeListener
import com.example.core.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity(), OnDataStateChangeListener {

    override fun onDataStateChanged(dataState: DataState<*>) {
        println("Data state changed")
    }
}