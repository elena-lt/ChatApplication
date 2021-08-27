package com.example.data.repositories.main.chats

import com.example.core.models.ChatDialogDomain
import com.example.core.models.UserDomain
import com.example.core.repositories.ChatsRepository
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatsRepositoryImp @Inject constructor(
    private val dataSource: ChatsDataSource
) : ChatsRepository {

    override suspend fun loadChats(): Flow<DataState<MutableList<ChatDialogDomain>>> =
        dataSource.loadAllChats()


    override suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>> =
        dataSource.findUser()

    override suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>> =
        dataSource.startNewChat(userId)
}