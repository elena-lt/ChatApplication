package com.example.data.persistance

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.persistance.entities.ChatEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class ChatsDaoTest {

    lateinit var database: AppDatabase
    lateinit var dao: ChatsDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries()
                .build()
        dao = database.getChatDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertChats() {
        val chat = ChatEntity("1")

        runBlocking {
            dao.insertChats(chat)
            val result = dao.getAllChats().first().get(0)
            assertThat(result).isEqualTo(chat)
        }
    }

    @Test
    fun getAllChats() {
    }

    @Test
    fun getChats() {
    }

    @Test
    fun deleteAllChats() {
    }

}