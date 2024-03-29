package com.example.chatapplication.models

import android.graphics.Bitmap

sealed class Models {

    data class User(
        val id: Int,
        val login: String,
        val fullName: String? = null,
        val email: String? = null,
        val profileImg: Bitmap? = null,
        val externalId: String? = null,

        ) : Models()


    data class Chat(
        var dialogId: String? = null,
        val lastMessage: String? = null,
        val lastMessageDateSent: Long = 0,
        val lastMessageUserId: Int? = null,
        val photo: String? = null,
        val userId: Int? = null,
        val roomJid: String? = null,
        val unreadMessageCount: Int? = null,
        val name: String? = null,
        val occupantsIds: List<Int>? = null,
        val type: Int? = null,
        val img: Bitmap? = null
    ) : Models()

}
