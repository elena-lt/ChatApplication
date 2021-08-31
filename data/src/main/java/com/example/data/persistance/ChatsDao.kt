package com.example.data.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.persistance.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
 interface ChatsDao {

     @Insert (onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertChats(chat: ChatEntity)

     @Query ("SELECT * FROM chats")
     fun getAllChats(): Flow<List<ChatEntity>>

     @Query ("SELECT * FROM chats")
     fun getChats(): List<ChatEntity>
}