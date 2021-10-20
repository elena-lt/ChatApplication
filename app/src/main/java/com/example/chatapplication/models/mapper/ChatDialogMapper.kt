package com.example.chatapplication.models.mapper

import com.example.chatapplication.models.Models
import com.example.core.models.ChatDialogDomain

object ChatDialogMapper {

    fun toChatDialog(chat: ChatDialogDomain): Models.Chat {
        return Models.Chat(
            dialogId = chat.dialogId,
            lastMessage = chat.lastMessage,
            lastMessageDateSent = chat.lastMessageDateSent,
//            chat.lastMessageUserId,
//            chat.photo,
//            chat.userId,
//            chat.roomJid,
            unreadMessageCount = chat.unreadMessageCount,
            name = chat.name,
//            chat.occupantsIds,
//            chat.type
        )
    }
}