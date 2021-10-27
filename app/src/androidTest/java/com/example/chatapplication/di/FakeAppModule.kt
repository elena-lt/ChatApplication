package com.example.chatapplication.di

import com.example.chatapplication.fakeDataSources.account.FakeAccountDataSource
import com.example.chatapplication.fakeDataSources.account.FakeAccountRepository
import com.example.chatapplication.fakeDataSources.chats.FakeChatsDataSourceImp
import com.example.chatapplication.fakeDataSources.chats.FakeChatsRepository
import com.example.core.repositories.AccountRepository
import com.example.core.repositories.ChatsRepository
import com.example.data.di.main.AccountModule
import com.example.data.di.main.ChatsModule
import com.example.data.repositories.main.account.AccountDataSource
import com.example.data.repositories.main.chats.ChatsDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@TestInstallIn(
    components = [
//        ViewModelComponent::class,
        SingletonComponent::class,
//        ActivityComponent::class
    ],
    replaces = [AccountModule::class, ChatsModule::class]
)
@Module
object FakeAppModule {

    @Provides
    fun provideChatsFakeDataSource(): ChatsDataSource =
        FakeChatsDataSourceImp()

    @Provides
    fun provideChatsFakeRepository(dataSource: ChatsDataSource): ChatsRepository =
        FakeChatsRepository(dataSource)

    @Provides
    fun provideAccountFakeDataSource(): AccountDataSource = FakeAccountDataSource()

    @Provides
    fun provideFakeAccountRepository(dataSource: AccountDataSource): AccountRepository =
        FakeAccountRepository(dataSource)

}