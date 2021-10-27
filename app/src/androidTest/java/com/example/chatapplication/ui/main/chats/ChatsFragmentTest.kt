package com.example.chatapplication.ui.main.chats

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chatapplication.ui.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@InternalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatsFragmentTest {

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

//    private lateinit var fragmentScenario: FragmentScenario<ChatsFragment>

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
//        fragmentScenario.moveToState(Lifecycle.State.STARTED)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun render_chatsList_test() {
        launchFragmentInHiltContainer<ChatsFragment> {
            ChatsFragment().also {

            }
        }

//        fragmentScenario.onFragment { fragment ->
//            fragment.setUpRvData(chats = ChatsViewState.Chats(chats = mutableListOf()))
//        }
    }
}