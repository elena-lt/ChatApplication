package com.example.chatapplication.ui.authentication.mvi

sealed class AuthenticationStateEvent {

    data class SignUpUser(
        val login: String,
        val username: String,
        val password: String,
    ) : AuthenticationStateEvent()

    data class LoginUser(
        val login: String,
        val password: String
    ) : AuthenticationStateEvent()
}