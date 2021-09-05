package com.example.chatapplication.ui.authentication.mvi

import com.example.chatapplication.models.Models

data class AuthenticationState(
    val error: Error? = null,
    val success: Success? = null
) {

    object Idle
    data class Error(val errorMessage: String?)
    data class Success(val user: Models.User)
}