package com.example.data.persistance

import androidx.test.filters.SmallTest
import com.example.data.persistance.entities.ChatEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class ChatsDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    lateinit var dao: ChatsDao

    @Before
    fun setup() {
//        val context = InstrumentationRegistry.getInstrumentation().context
//        database =
//            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries()
//                .build()
        hiltRule.inject()
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
            val result = dao.getChats()[0]
            assertThat(result).isEqualTo(chat)
        }
    }

    @Test
    fun deleteAllChats() {
        val chat1 = ChatEntity("1")
        val chat2 = ChatEntity("2")
        val chat3 = ChatEntity("3")

        runBlocking {
            dao.insertChats(chat1)
            dao.insertChats(chat2)
            dao.insertChats(chat3)

            dao.deleteAllChats()

            val result = dao.getChats()
            assertThat(result).isEmpty()
        }
    }

}