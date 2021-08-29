package com.example.data.mappers

import com.example.core.models.ChatMessageDomain
import com.quickblox.chat.model.QBChatMessage

object ChatMessageMapper {

    fun toChatMessageDomain(chatMgs: QBChatMessage): ChatMessageDomain {
        return ChatMessageDomain(
            chatMgs.id,
            chatMgs.dialogId,
            chatMgs.dateSent,
            chatMgs.body,
            chatMgs.readIds,
            chatMgs.deliveredIds,
            chatMgs.recipientId,
            chatMgs.senderId

        )
    }
}