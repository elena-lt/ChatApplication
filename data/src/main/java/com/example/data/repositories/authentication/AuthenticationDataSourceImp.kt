package com.example.data.repositories.authentication

import android.os.Bundle
import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.repositories.authentication.AuthenticationDataSource
import com.example.data.sessionManager.SessionManger
import com.quickblox.auth.session.QBSession
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSessionParameters
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.log

class AuthenticationDataSourceImp @Inject constructor(
    val sessionManager: SessionManger,
    val qbChatService: QBChatService
) : AuthenticationDataSource {

    @ExperimentalCoroutinesApi
    override fun signUpUser(
        username: String,
        password: String
    ): Flow<DataState<UserDomain>> {

        return callbackFlow {
            offer(DataState.LOADING(true))
            val user = QBUser()
            user.login = username
            user.password = password
            QBUsers.signUp(user).performAsync(object : QBEntityCallback<QBUser> {
                override fun onSuccess(p0: QBUser?, p1: Bundle?) {
                    offer(DataState.SUCCESS(UserMapper.toUserDomain(p0)))
                }

                override fun onError(p0: QBResponseException?) {
                    offer(DataState.ERROR(p0?.message ?: "Unknown error"))
                }
            })
            awaitClose()
        }
    }

    override suspend fun loginUser(login: String, password: String): Flow<DataState<UserDomain>> =
        flow {
            emit(DataState.LOADING(true))

            val user = QBUser(login, password)
            val query = QBUsers.signIn(user)
            kotlin.runCatching {
                query.perform()
            }.onSuccess {
                emit(DataState.SUCCESS(UserMapper.toUserDomain(it)))
//                sessionManager.createSessionManagerListener()
                createChatService(user)
            }.onFailure {
                emit(DataState.ERROR<UserDomain>(errorMessage = it.toString()))
            }

        }


    private fun createChatService(user: QBUser) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                qbChatService.isReconnectionAllowed = true
                qbChatService.setUseStreamManagement(true)
                qbChatService.login(user, object : QBEntityCallback<Void> {
                    override fun onSuccess(p0: Void?, p1: Bundle?) {
                        Log.d("AppDebug", "onSuccess: chat service created")
                    }

                    override fun onError(p0: QBResponseException?) {
                        Log.d("AppDebug", "onError: chat service not created ${p0?.message}")
                    }
                })
            }
        }
    }
}