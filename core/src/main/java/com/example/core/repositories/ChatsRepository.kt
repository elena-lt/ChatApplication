package com.example.core.repositories

import com.example.core.models.ChatDialogDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {

    suspend fun loadChats(): Flow<DataState<MutableList<ChatDialogDomain>>>
    suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>>
    suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>>
}