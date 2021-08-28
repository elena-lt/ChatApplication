package com.example.chatapplication.models.mapper

import com.example.chatapplication.models.Models
import com.example.core.models.ChatDialogDomain

object ChatDialogMapper {

    fun toChatDialog(chat: ChatDialogDomain): Models.Chat {
        return Models.Chat(
            chat.dialogId,
            chat.lastMessage,
            chat.lastMessageDateSent,
            chat.lastMessageUserId,
            chat.photo,
            chat.userId,
            chat.roomJid,
            chat.unreadMessageCount,
            chat.name,
            chat.occupantsIds,
            chat.type
        )
    }
}