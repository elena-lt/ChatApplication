package com.example.data.mappers

import com.example.core.models.ChatDialogDomain
import com.example.data.persistance.entities.ChatEntity
import com.quickblox.chat.model.QBChatDialog

object ChatDialogMapper {

    fun toChatDialogDomain(chat: QBChatDialog?): ChatDialogDomain {
        return ChatDialogDomain(
            chat?.dialogId,
            chat?.lastMessage,
            chat?.lastMessageDateSent ?: 0,
            chat?.lastMessageUserId,
            chat?.photo,
            chat?.userId,
            chat?.roomJid,
            chat?.unreadMessageCount,
            chat?.name,
            chat?.occupants,
            chat?.type?.code
        )
    }

    fun toChatDialogDomain(chat: ChatEntity): ChatDialogDomain {
        return ChatDialogDomain(
            chat.dialogId,
            chat.lastMessage,
//            chat.lastMessageDateSent,
//            chat.lastMessageUserId,
//            chat.photo,
//            chat.userId,
//            chat.roomJid,
//            chat.unreadMessageCount,
//            chat.name,
//            null,
//            chat.type
        )
    }


    fun toChatEntity(chat: QBChatDialog): ChatEntity {
        return ChatEntity(
            chat.dialogId,
            chat.lastMessage,
//            chat.lastMessageDateSent,
//            chat.lastMessageUserId,
//            chat.photo,
//            chat.userId,
//            chat.roomJid,
//            chat.unreadMessageCount,
//            chat.name,
//            null
        )
    }
}