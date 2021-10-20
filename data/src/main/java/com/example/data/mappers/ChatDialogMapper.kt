package com.example.data.mappers

import com.example.core.models.ChatDialogDomain
import com.example.data.persistance.entities.ChatEntity
import com.quickblox.chat.model.QBChatDialog

object ChatDialogMapper {

    fun toChatDialogDomain(chat: QBChatDialog?): ChatDialogDomain {
        return ChatDialogDomain(
            dialogId = chat?.dialogId,
            lastMessage = chat?.lastMessage,
            lastMessageDateSent = chat?.lastMessageDateSent ?: 0,
            lastMessageUserId = chat?.lastMessageUserId,
            photo = chat?.photo,
            userId = chat?.userId,
            roomJid = chat?.roomJid,
            unreadMessageCount = chat?.unreadMessageCount,
            name = chat?.name,
            occupantsIds = chat?.occupants,
            type = chat?.type?.code
        )
    }

    fun toChatDialogDomain(chat: ChatEntity): ChatDialogDomain {
        return ChatDialogDomain(
            chat.dialogId,
            chat.lastMessage,
            chat.lastMessageDateSent,
//            chat.lastMessageUserId,
//            chat.photo,
//            chat.userId,
//            chat.roomJid,
            chat.unreadMessageCount,
            chat.name,
//            null,
//            chat.type
        )
    }

    fun toChatEntity(chat: QBChatDialog?): ChatEntity {
         return chat?.let {
            return@let ChatEntity(
                it.dialogId,
                it.lastMessage,
                it.lastMessageDateSent,
                it.unreadMessageCount,
                it.name,
            )
        } ?: return ChatEntity("-1")
    }
}