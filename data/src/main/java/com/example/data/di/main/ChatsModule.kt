package com.example.data.di.main

import com.example.core.repositories.ChatsRepository
import com.example.data.repositories.main.chats.ChatDataSourceImp
import com.example.data.repositories.main.chats.ChatsDataSource
import com.example.data.repositories.main.chats.ChatsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatsModule {

    @Provides
    @Singleton
    fun provideChatDataSource(): ChatsDataSource = ChatDataSourceImp()

    @Provides
    @Singleton
    fun provideChatsRepository(dataSource: ChatsDataSource): ChatsRepository =
        ChatsRepositoryImp(dataSource)
}