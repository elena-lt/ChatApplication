package com.example.data.repositories.main.chats

import android.os.Bundle
import android.util.Log
import com.example.core.models.ChatDialogDomain
import com.example.core.models.ChatMessageDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.ChatDialogMapper
import com.example.data.mappers.ChatMessageMapper
import com.example.data.mappers.UserMapper
import com.example.data.utils.Const
import com.example.data.utils.Const.MESSAGE_DELIVERED
import com.example.data.utils.Const.MESSAGE_NOT_DELIVERED
import com.example.data.utils.Const.TAG
import com.example.data.utils.Const.UNKNOWN_ERROR
import com.quickblox.chat.QBChat
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.chat.model.QBDialogType
import com.quickblox.chat.request.QBMessageGetBuilder
import com.quickblox.chat.utils.DialogUtils
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.QBRequestGetBuilder
import com.quickblox.users.QBUsers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jivesoftware.smack.SmackException
import java.lang.NullPointerException
import javax.inject.Inject
import kotlin.math.log

@ExperimentalCoroutinesApi
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
            it?.let {
                val chatList = it.map { chatDialog ->
                    ChatDialogMapper.toChatDialogDomain(chatDialog)
                }.toMutableList()
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

        QBRestChatService.createChatDialog(dialog)
            .performAsync(object : QBEntityCallback<QBChatDialog> {
                override fun onSuccess(p0: QBChatDialog?, p1: Bundle?) {
                    (DataState.SUCCESS(ChatDialogMapper.toChatDialogDomain(p0)))
                }

                override fun onError(p0: QBResponseException?) {
                    Log.d("AppDebug", "onError: ${p0?.message}")
                }

            })
    }

    override suspend fun retrieveChatHistory(chatId: String): Flow<DataState<MutableList<ChatMessageDomain>>> =
        flow {
            emit(DataState.LOADING(true))

            val dialog = getDialogById(chatId)
            if (dialog != null) {
                val messageGetBuilder = QBMessageGetBuilder()
                messageGetBuilder.limit = 50

                kotlin.runCatching {
                    QBRestChatService.getDialogMessages(dialog, messageGetBuilder).perform()
                }.onSuccess {
                    val list = it.map {
                        ChatMessageMapper.toChatMessageDomain(it)
                    }.toMutableList()
                    emit(DataState.SUCCESS(data = list))

                }.onFailure {
                    emit(DataState.ERROR<MutableList<ChatMessageDomain>>(errorMessage = it.toString()))
                }
            }

        }

    private fun getDialogById(chatId: String): QBChatDialog? {
        kotlin.runCatching {
            QBRestChatService.getChatDialogById(chatId).perform()
        }.onSuccess {
            return it
        }.onFailure {
            Log.d(TAG, "getDialogById: ERROR $it")
            return null
        }
        return null
    }


    override suspend fun sendMessage(
        messageContent: String,
        chatId: String,
        occupantsIds: List<Int>
    ): Flow<DataState<String>> = flow {
        val dialog = QBChatDialog()
        dialog.setOccupantsIds(occupantsIds)
        dialog.initForChat(chatId, QBDialogType.PRIVATE, QBChatService.getInstance())

        val chatMessage = QBChatMessage()
        chatMessage.body = messageContent
        chatMessage.setSaveToHistory(true)

        kotlin.runCatching {
            dialog.sendMessage(chatMessage)
        }.onSuccess {
//            when (verifyIfMessageDelivered(dialog)) {
//                MESSAGE_DELIVERED -> emit(DataState.SUCCESS<String>(data = MESSAGE_DELIVERED))
//                MESSAGE_NOT_DELIVERED -> emit(DataState.SUCCESS<String>(errorMessage = MESSAGE_NOT_DELIVERED))
//            }
            DataState.SUCCESS<String>(data = MESSAGE_DELIVERED)
        }.onFailure {
            Log.d("AppDebug", "sendMessage: error $it")
            emit(DataState.ERROR<String>(it.message ?: UNKNOWN_ERROR))
        }
    }

    private suspend fun verifyIfMessageDelivered(dialog: QBChatDialog): String {
        Log.d("AppDebug", "verifyIfMessageDelivered: ${Thread.currentThread().name}")

        return suspendCancellableCoroutine { cont ->
            dialog.addMessageSentListener(object : QBChatDialogMessageSentListener {
                override fun processMessageSent(dialogId: String?, p1: QBChatMessage?) {
                    Log.d(TAG, "processMessageSent: message delivered")
                    cont.resume(MESSAGE_DELIVERED, null)
                    Log.d(TAG, "processMessageSent: ${cont.isActive}")
                }

                override fun processMessageFailed(dialogId: String?, p1: QBChatMessage?) {
                    Log.d("AppDebug", "processMessageFailed: not delivered")
                    cont.resume(MESSAGE_NOT_DELIVERED, onCancellation = null)
                }
            })
            cont.cancel()
        }
    }
}
