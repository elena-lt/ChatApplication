package com.example.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.persistance.entities.AccountProperties
import com.example.data.persistance.typeConverters.ImageConverter

@Database(
    entities = [AccountProperties::class],
    version = 1
)
@TypeConverters(ImageConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

}