package com.example.data.persistance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.data.persistance.entities.AccountPropertiesEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class AccountPropertiesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    lateinit var dao: AccountPropertiesDao

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        dao = database.getAccountPropertiesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAccountProperties() {
        runBlocking {
            val ac = AccountPropertiesEntity(id = 1, "user1")
            dao.insertAccountProperties(ac)
            val result = dao.searchByUserId(1)
            assertThat(result).isEqualTo(ac)
        }
    }

    @Test
    fun updateAccountProperties() {
        runBlocking {
            val ac = AccountPropertiesEntity(id = 1, "user1", "Test User")
            dao.insertAccountProperties(ac)

            val updatedAC =
                AccountPropertiesEntity(ac.id, "user1", "test@test.com", "Testing User", null, "")

            dao.updateAccountProperties(
                id = 1,
                updatedAC.login,
                updatedAC.email ?: "",
                updatedAC.fullName ?: "",
                null,
                ""
            )
            val result = dao.searchByUserId(1)?.email

            assertThat(result).isEqualTo(updatedAC.email)
        }
    }

}