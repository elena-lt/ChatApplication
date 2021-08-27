package com.example.chatapplication.models

sealed class Models {

    data class User(
        val id: Int? = null,
        val login: String? = null,
        val fullName: String? = null,
        val email: String? = null,
        val externalId: String? = null,

        ): Models()
}
