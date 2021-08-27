package com.example.data.mappers

import com.example.core.models.ChatDialogDomain
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
}