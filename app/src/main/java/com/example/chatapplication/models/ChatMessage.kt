package com.example.chatapplication.models

import android.graphics.Bitmap

data class ChatMessage(
    var id: String? = null,
    val dialogId: String? = null,
    val dateSent: Long = 0L,
    val body: String? = null,
    val readIds: Collection<Int>? = null,
    val deliveredIds: Collection<Int>? = null,
    val recipientId: Int? = null,
    val senderId: Int? = null,
    val img: Bitmap? = null
)