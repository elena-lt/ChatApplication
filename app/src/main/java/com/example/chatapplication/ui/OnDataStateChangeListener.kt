package com.example.chatapplication.ui

import com.example.core.utils.DataState

interface OnDataStateChangeListener {

    fun onDataStateChanged(dataState: DataState<*>)
}