package com.example.data.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.data.persistance.entities.ChatEntity

@Dao
 interface ChatsDao {

     @Insert (onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertChats(chat: ChatEntity)
}