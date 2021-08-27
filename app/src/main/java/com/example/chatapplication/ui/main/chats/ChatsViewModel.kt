package com.example.chatapplication.ui.main.chats

import androidx.lifecycle.viewModelScope
import com.example.chatapplication.models.mapper.UserMapper
import com.example.chatapplication.ui.base.BaseViewModel
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent.*
import com.example.chatapplication.ui.main.chats.mvi.ChatsViewState
import com.example.core.usecases.main.chats.FindUserUseCase
import com.example.core.usecases.main.chats.LoadAllChatsUseCase
import com.example.core.usecases.main.chats.StartNewChatUseCase
import com.example.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val loadChatsUseCase: LoadAllChatsUseCase,
    private val findUser: FindUserUseCase,
    private val startNewChat: StartNewChatUseCase
) : BaseViewModel<ChatsViewState, ChatsStateEvent, DataState<*>>() {

    override fun handleStateEvent(stateEvent: ChatsStateEvent) {
        when (stateEvent) {
            is LoadAllChats -> loadAllChats()
            is FindUser -> findUser()
            is StartNewChat -> startNewDialog(stateEvent.userId)
        }
    }

    private fun loadAllChats() {
        viewModelScope.launch {
            loadChatsUseCase.invoke().collect {
                setDataState(it)
            }
        }
    }

    private fun findUser() {
        viewModelScope.launch {
            findUser.invoke().collect {
                setDataState(it)

                it.data?.let {
                    setViewState(
                        ChatsViewState.Users(users = it.map {
                            UserMapper.toUser(it)
                        }.toMutableList())
                    )
                }
            }
        }
    }

    private fun startNewDialog(userId: Int) {
        viewModelScope.launch {
            startNewChat.invoke(userId).collect { dataState ->
                setDataState(dataState)

                dataState.data?.let {

                }
            }
        }
    }

    override fun createInitialState(): ChatsViewState = ChatsViewState.Idle
}