package com.example.chatapplication.ui.authentication.mvi

import com.example.chatapplication.models.Models

sealed class AuthenticationState {

    object Idle : AuthenticationState()
    data class Success(val user: Models.User) : AuthenticationState()
}