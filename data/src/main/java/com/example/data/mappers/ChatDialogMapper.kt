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
            userId = chat.userId,
//            chat.roomJid,
            unreadMessageCount = chat.unreadMessageCount,
            name = chat.name,
            occupantsIds = listOf(chat.occupant1, chat.occupant2)
//            chat.type
        )
    }

    fun toChatEntity(chat: QBChatDialog?): ChatEntity {
        return chat?.let {
            return@let ChatEntity(
                it.dialogId,
                it.lastMessage,
                it.lastMessageDateSent,
                it.userId,
                it.unreadMessageCount,
                it.name,
                it.occupants[0],
                it.occupants[1]
            )
        } ?: return ChatEntity("-1")
    }
}