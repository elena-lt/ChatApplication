package com.example.chatapplication.ui.main.chats

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.chatapplication.R
import com.example.chatapplication.recyclerViewUtils.RecyclerViewHolder
import com.example.chatapplication.ui.launchFragmentInHiltContainer
import com.example.chatapplication.ui.main.chats.mvi.ChatsViewState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@InternalCoroutinesApi
@HiltAndroidTest
@LargeTest
@RunWith(AndroidJUnit4::class)
class ChatsFragmentTest {

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun setupRvData_emptyChatList_setVisibleInformationMsg() {
        launchFragmentInHiltContainer<ChatsFragment>(themeResId = R.id.chatsFragment) {

           (this as ChatsFragment).setUpRvData(ChatsViewState.Chats(chats = mutableListOf()))
        }

        onView(withId(R.id.rv_chats)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.tv_no_chats)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.tv_no_chats)).check(matches(withText(R.string.no_active_chats_found)))
    }

    @Test
    fun btnClicked_navigateToAccountFragment(){
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ChatsFragment>{
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.btn_user_profile_image)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            R.id.accountFragment
        )
    }

    @Test
    fun btnClicked_navigateToStartNewChatFragment(){
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ChatsFragment>{
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.btn_start_new_chat)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            R.id.action_chatsFragment_to_newChatFragment
        )
    }

}