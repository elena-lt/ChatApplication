package com.example.chatapplication.ui.main.chats

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.models.Models
import com.example.chatapplication.models.mapper.ChatDialogMapper
import com.example.chatapplication.models.mapper.ChatMessageMapper
import com.example.chatapplication.models.mapper.UserMapper
import com.example.chatapplication.ui.base.BaseViewModel
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent.*
import com.example.chatapplication.ui.main.chats.mvi.ChatsViewState
import com.example.core.usecases.main.account.LoadAccountPropertiesUseCase
import com.example.core.usecases.main.chats.*
import com.example.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val loadCurrUserAccount: LoadAccountPropertiesUseCase,
    private val loadChatsUseCase: LoadAllChatsUseCase,
    private val findUser: FindUserUseCase,
    private val startNewChat: StartNewChatUseCase,
    private val sendMessage: SendMessageUseCase,
    private val retrieveChatHistory: RetrieveChatHistoryUseCase
) : BaseViewModel<ChatsViewState, ChatsStateEvent, DataState<*>>() {

    override fun handleStateEvent(stateEvent: ChatsStateEvent) {
        when (stateEvent) {
            is LoadCurrUserAccount -> loadCurrUserAccount()
            is LoadAllChats -> loadAllChats()
            is FindUser -> findUser()
            is StartNewChat -> startNewDialog(stateEvent.userId)
            is RetrieveChatHistory -> retrieveChatHistory(stateEvent.chatId)
            is SendMessage -> sendMessage(
                stateEvent.messageContent,
                stateEvent.chatId,
                stateEvent.occupantsIds
            )
        }
    }

    private fun loadCurrUserAccount() {
        viewModelScope.launch {
            loadCurrUserAccount.invoke().collect { dataState ->

                dataState.data?.let {
                    setViewState(
                        currentState.copy(
                            currUser = ChatsViewState.CurrLoggedInUser(
                                user = UserMapper.toUser(
                                    it
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private fun loadAllChats() {
        viewModelScope.launch {
            loadChatsUseCase.invoke().collect { dataState ->
                setDataState(dataState)

                dataState.data.let { chatList ->
                    chatList?.let {
                        setViewState(currentState.copy(chats = ChatsViewState.Chats(it.map { chat ->
                            ChatDialogMapper.toChatDialog(chat)
                        }.toMutableList())))
                    }
                }
            }
        }
    }

    private fun findUser() {
        viewModelScope.launch {
            findUser.invoke().collect {
                setDataState(it)

                it.data?.let { usersList ->
                    val newState =
                        currentState.copy(users = ChatsViewState.Users(usersList.map { user ->
                            UserMapper.toUser(user)
                        }.toMutableList()))
                    setViewState(newState)
                    Log.d("AppDebug", "findUser: viewStateSet: $newState")
                }
            }
        }
    }

    private fun startNewDialog(userId: Int) {
        viewModelScope.launch {
            startNewChat.invoke(userId).collect { dataState ->
                setDataState(dataState)

                dataState.data?.let {
                    setViewState(
                        currentState.copy(
                            openChatDialog = ChatsViewState.OpenChatDialog(
                                chatDialog = ChatDialogMapper.toChatDialog(it),
                                messages = null
                            )
                        )
                    )
                    Log.d("AppDebug", "START NEW DIALOG VIEW STATE: ${currentState.openChatDialog.toString()}")
                }
            }
        }
    }

    private fun retrieveChatHistory(chatId: String) {
        viewModelScope.launch {
            retrieveChatHistory.invoke(chatId).collect { dataState ->
                setDataState(dataState)

                dataState.data?.let {
                    setViewState(
                        currentState.copy(
                            openChatDialog = ChatsViewState.OpenChatDialog(
                                chatDialog = currentState.openChatDialog!!.chatDialog,
                                messages = it.map { msg -> ChatMessageMapper.toChatMessage(msg) }
                                    .toMutableList()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun sendMessage(messageContent: String, chatId: String, occupantsIds: List<Int>) {
        viewModelScope.launch {
            sendMessage.invoke(messageContent, chatId, occupantsIds).collect { dataState ->
                setDataState(dataState)

                dataState.data?.let {

                }
            }
        }
    }

    override fun createInitialState(): ChatsViewState = ChatsViewState()
}