package com.example.data.di.main

import com.example.core.repositories.ChatsRepository
import com.example.data.persistance.AppDatabase
import com.example.data.persistance.ChatsDao
import com.example.data.repositories.main.chats.ChatDataSourceImp
import com.example.data.repositories.main.chats.ChatsDataSource
import com.example.data.repositories.main.chats.ChatsRepositoryImp
import com.example.data.utils.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatsModule {

    @Provides
    @Singleton
    fun provideChatsDao(database: AppDatabase): ChatsDao = database.getChatDao()

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideChatDataSource(
        connectivityManager: ConnectivityManager,
        chatsDao: ChatsDao
    ): ChatsDataSource = ChatDataSourceImp(connectivityManager, chatsDao)

    @Provides
    @Singleton
    fun provideChatsRepository(dataSource: ChatsDataSource): ChatsRepository =
        ChatsRepositoryImp(dataSource)
}