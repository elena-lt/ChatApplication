package com.example.data.repositories.main.chats

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.example.core.models.ChatDialogDomain
import com.example.core.models.ChatMessageDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.ChatDialogMapper
import com.example.data.mappers.ChatMessageMapper
import com.example.data.mappers.UserMapper
import com.example.data.persistance.ChatsDao
import com.example.data.persistance.entities.ChatEntity
import com.example.data.repositories.NetworkBoundResource
import com.example.data.utils.ConnectivityManager
import com.example.data.utils.Const
import com.example.data.utils.Const.MESSAGE_DELIVERED
import com.example.data.utils.Const.MESSAGE_NOT_DELIVERED
import com.example.data.utils.Const.TAG
import com.example.data.utils.Const.UNKNOWN_ERROR
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jivesoftware.smack.chat.ChatManager
import javax.inject.Inject
import kotlin.math.log

@ExperimentalCoroutinesApi
class ChatDataSourceImp @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val sharedPreferences: SharedPreferences,
    private val chatsDao: ChatsDao
) : ChatsDataSource {

    override suspend fun loadAllChats(): Flow<DataState<MutableList<ChatDialogDomain>>> {

        val requestBuilder = QBRequestGetBuilder()
        requestBuilder.limit = 50

        return object :
            NetworkBoundResource<MutableList<ChatEntity>, MutableList<ChatDialogDomain>>(
                connectivityManager
            ) {
            override suspend fun loadFromDB(): DataState<MutableList<ChatDialogDomain>> {
                val list = chatsDao.getChats().map {
                    ChatDialogMapper.toChatDialogDomain(it)
                }.toMutableList()
                return DataState.SUCCESS(list)
            }

            override suspend fun createCall(): MutableList<ChatEntity>? {
                var list: MutableList<ChatEntity>? = null
                kotlin.runCatching {
                    QBRestChatService.getChatDialogs(null, requestBuilder).perform()
                }.onSuccess {
                    list = it.map { chatDialog ->
                        ChatDialogMapper.toChatEntity(chatDialog)
                    }.toMutableList()
                }.onFailure {
                    onFetchFailed(it.toString())
                }
                return list
            }

            override suspend fun saveFetchResult(data: MutableList<ChatEntity>?) {
                chatsDao.deleteAllChats()
                data?.let {
                    for (item in it) {
                        chatsDao.insertChats(item)
                    }
                }
            }
        }.flow
    }

    override suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>> =
        flow<DataState<MutableList<UserDomain>>> {

            emit(DataState.LOADING(true))

            kotlin.runCatching {
                QBUsers.getUsers(null).perform()
            }.onSuccess {data  ->
                data?.let {userList ->
                    val currUserLogin = sharedPreferences.getString(Const.SP_USER_LOGIN, "")!!
                    val usersList: MutableList<UserDomain> = userList.filter {
                        it.login != currUserLogin
                    }.map { userItem ->
                        UserMapper.toUserDomain(userItem)
                    }.toMutableList()
                    emit(DataState.SUCCESS(usersList))
                } ?: emit(DataState.SUCCESS(errorMessage = "empty list"))

            }.onFailure {
                emit(DataState.ERROR(it.toString()))
            }
        }

    override suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>> =
        flow<DataState<ChatDialogDomain>> {
            emit(DataState.LOADING(true))
            val dialog: QBChatDialog? = DialogUtils.buildPrivateDialog(userId)

            kotlin.runCatching {
                QBRestChatService.createChatDialog(dialog).perform()
            }.onFailure {
                emit(DataState.ERROR(errorMessage = it.message ?: "UNKNOWN ERROR"))
            }.onSuccess {
                emit(DataState.SUCCESS(ChatDialogMapper.toChatDialogDomain(it)))
            }
        }

    override suspend fun retrieveChatHistory(chatId: String): Flow<DataState<MutableList<ChatMessageDomain>>> =
        flow<DataState<MutableList<ChatMessageDomain>>> {
            emit(DataState.LOADING(true))

            val dialog = getDialogById(chatId)
            if (dialog != null) {
                val messageGetBuilder = QBMessageGetBuilder()
                messageGetBuilder.limit = 50

                kotlin.runCatching {
                    QBRestChatService.getDialogMessages(dialog, messageGetBuilder).perform()
                }.onSuccess {
                    val list = it.map { chatMessage ->
                        ChatMessageMapper.toChatMessageDomain(chatMessage)
                    }.toMutableList()
                    emit(DataState.SUCCESS(data = list))

                }.onFailure {
                    emit(DataState.ERROR(errorMessage = it.toString()))
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
    ): Flow<DataState<String>> = flow<DataState<String>> {
        val dialog = QBChatDialog()
        dialog.setOccupantsIds(occupantsIds)

        Log.d("AppDebug", "QBChatService is null: ${QBChatService.getInstance().user == null}")

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
            DataState.SUCCESS(data = MESSAGE_DELIVERED)
        }.onFailure {
            Log.d("AppDebug", "sendMessage: error $it")
            emit(DataState.ERROR(it.message ?: UNKNOWN_ERROR))
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
