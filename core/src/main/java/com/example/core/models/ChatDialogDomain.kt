package com.example.core.models

data class ChatDialogDomain(

    var dialogId: String? = null,
    val lastMessage: String? = null,
    val lastMessageDateSent: Long = 0,
//    val lastMessageUserId: Int? = null,
//    val photo: String? = null,
//    val userId: Int? = null,
//    val roomJid: String? = null,
    val unreadMessageCount: Int? = null,
    val name: String? = null,
//    val occupantsIds: List<Int>? = null,
//    val type: Int? = null,
)
