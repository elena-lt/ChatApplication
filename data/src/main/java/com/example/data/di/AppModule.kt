package com.example.data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.core.repositories.AuthenticationRepository
import com.example.data.persistance.AppDatabase
import com.example.data.repositories.authentication.AuthenticationDataSource
import com.example.data.repositories.authentication.AuthenticationDataSourceImp
import com.example.data.repositories.authentication.AuthenticationRepositoryImp
import com.example.data.utils.ConnectivityManager
import com.example.data.utils.Const.DATABASE_NAME
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.chat.QBChatService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return ConnectivityManager(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQbSessionManager(): QBSessionManager = QBSessionManager.getInstance()

    @Provides
    @Singleton
    fun provideQbChatService(): QBChatService = QBChatService.getInstance()
}