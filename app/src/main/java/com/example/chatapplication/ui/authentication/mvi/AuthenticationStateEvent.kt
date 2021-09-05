package com.example.chatapplication.ui.authentication.mvi

import java.io.File

sealed class AuthenticationStateEvent {

    data class SignUpUser(
        val username: String,
        val email: String,
        val fullName: String,
        val password: String,
        val confirmPassword: String,
        val image: File? = null
    ) : AuthenticationStateEvent()

    data class LoginUser(
        val login: String,
        val password: String
    ) : AuthenticationStateEvent()
}