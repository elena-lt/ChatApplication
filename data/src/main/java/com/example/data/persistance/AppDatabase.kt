package com.example.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.persistance.entities.AccountPropertiesEntity
import com.example.data.persistance.entities.ChatEntity
import com.example.data.persistance.typeConverters.ImageConverter

@Database(
    entities = [AccountPropertiesEntity::class, ChatEntity::class],
    version = 1
)
@TypeConverters(ImageConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getChatDao(): ChatsDao

}