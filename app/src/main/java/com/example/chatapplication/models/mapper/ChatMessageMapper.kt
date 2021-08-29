package com.example.chatapplication.models.mapper

import com.example.chatapplication.models.ChatMessage
import com.example.core.models.ChatMessageDomain

object ChatMessageMapper {

    fun toChatMessage(chatMsg: ChatMessageDomain): ChatMessage {
        return ChatMessage(
            chatMsg.id,
            chatMsg.dialogId,
            chatMsg.dateSent,
            chatMsg.body,
            chatMsg.readIds,
            chatMsg.deliveredIds,
            chatMsg.recipientId,
            chatMsg.senderId
        )
    }
}