package com.example.data.repositories.main.chats

import android.os.Bundle
import android.util.Log
import com.example.core.models.ChatDialogDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.ChatDialogMapper
import com.example.data.mappers.UserMapper
import com.example.data.utils.Const
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.utils.DialogUtils
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.QBRequestGetBuilder
import com.quickblox.users.QBUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatDataSourceImp @Inject constructor() : ChatsDataSource {

    override suspend fun loadAllChats(): Flow<DataState<MutableList<ChatDialogDomain>>> = flow {
        emit(DataState.LOADING(true))

        kotlin.runCatching {
            val requestBuilder = QBRequestGetBuilder()
            requestBuilder.limit = 50
//            requestBuilder.skip = 100
//            requestBuilder.sortAsc("last_message_date_sent")
            QBRestChatService.getChatDialogs(null, requestBuilder).perform()
        }.onSuccess {
            Log.d("AppDebug", "loadAllChats: ${it.toString()}")
            it?.let {
                val chatList = it.map { chatDialog ->
                    ChatDialogMapper.toChatDialogDomain(chatDialog)
                }.toMutableList()
                Log.d("AppDebug", "loadAllChats: ${chatList.toString()}")
                emit(DataState.SUCCESS(data = chatList))
            } ?: emit(
                DataState.SUCCESS<MutableList<ChatDialogDomain>>(
                    null,
                    errorMessage = "Empty list"
                )
            )

        }.onFailure {
            emit(DataState.ERROR<MutableList<ChatDialogDomain>>(it.message ?: "UNKNOWN ERROR"))
        }
    }

    override suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>> = flow {

        emit(DataState.LOADING(true))

        kotlin.runCatching {
            QBUsers.getUsers(null).perform()
        }.onSuccess {
            it?.let {
                val usersList: MutableList<UserDomain> = it.map { user ->
                    UserMapper.toUserDomain(user)
                }.toMutableList()
                emit(DataState.SUCCESS(usersList))
            } ?: emit(DataState.SUCCESS<MutableList<UserDomain>>(errorMessage = "empty list"))

        }.onFailure {
            emit(DataState.ERROR<MutableList<UserDomain>>(it.toString()))
        }
    }

    override suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>> = flow {
        emit(DataState.LOADING(true))
        val dialog: QBChatDialog = DialogUtils.buildPrivateDialog(userId)

        QBRestChatService.createChatDialog(dialog).performAsync(object : QBEntityCallback<QBChatDialog>{
            override fun onSuccess(p0: QBChatDialog?, p1: Bundle?) {
                (DataState.SUCCESS(ChatDialogMapper.toChatDialogDomain(p0)))
            }

            override fun onError(p0: QBResponseException?) {
                Log.d("AppDebug", "onError: ${p0?.message}")
            }

        })
    }
}
