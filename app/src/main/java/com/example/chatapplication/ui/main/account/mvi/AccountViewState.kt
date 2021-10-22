package com.example.chatapplication.ui.main.account.mvi

import com.example.chatapplication.models.Models

data class AccountViewState(
    val user: Models.User? = null,
    val loggedOut: Boolean = false
)