package com.example.chatapplication.fakeDataSources.chats

import com.example.core.models.ChatDialogDomain
import com.example.core.models.ChatMessageDomain
import com.example.core.models.UserDomain
import com.example.core.repositories.ChatsRepository
import com.example.core.utils.DataState
import com.example.data.repositories.main.chats.ChatsDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeChatsRepository @Inject constructor(private val dataSourceImp: ChatsDataSource) :
    ChatsRepository {

    override suspend fun loadChats(): Flow<DataState<MutableList<ChatDialogDomain>>> {
        return dataSourceImp.loadAllChats()
    }

    override suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>> {
        TODO("Not yet implemented")
    }

    override suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>> {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveChatHistory(chatID: String): Flow<DataState<MutableList<ChatMessageDomain>>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        messageContent: String,
        chatId: String,
        occupantsIds: List<Int>
    ): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }
}