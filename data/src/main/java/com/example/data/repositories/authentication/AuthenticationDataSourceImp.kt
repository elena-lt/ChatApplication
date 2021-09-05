@file:Suppress("RemoveExplicitTypeArguments")

package com.example.data.repositories.authentication

import android.os.Bundle
import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.repositories.NetworkBoundResource
import com.example.data.sessionManager.SessionManger
import com.quickblox.chat.QBChatService
import com.quickblox.content.QBContent
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.QBUsers.updateUser
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthenticationDataSourceImp @Inject constructor(
    val sessionManager: SessionManger,
    val qbChatService: QBChatService
) : AuthenticationDataSource {

    @ExperimentalCoroutinesApi
    override suspend fun signUpUser(
        username: String,
        email: String,
        password: String,
        profileImage: File?
    ): Flow<DataState<UserDomain>> = flow<DataState<UserDomain>> {
        val user = QBUser()
        user.login = username
        user.password = password
        user.email = email

        kotlin.runCatching {
            QBUsers.signUp(user).perform()
        }.onSuccess { qbUser ->
            loginUser(username, password).collect {
                it.data?.let {
                    profileImage?.let { file ->
                        val img = uploadImage(qbUser, file)
                        img?.let { fileId ->
                            updateUser(qbUser, fileId)
                        }
                    }
                }
            }
        }.onFailure {
            emit(DataState.ERROR(it.message ?: "Unknown error"))
        }
    }

    private suspend fun uploadImage(user: QBUser, image: File): Int? {

        return suspendCoroutine { cont ->
            kotlin.runCatching {
                QBContent.uploadFileTask(image, false, null).perform()
            }.onSuccess {
                cont.resume(it.id)
            }.onFailure {
                Log.d("AppDebug", "uploadImage: error $it")
                cont.resume(null)
            }
        }
    }

    private suspend fun updateUser(user: QBUser, fileId: Int): QBUser {
        return suspendCoroutine { cont ->
            kotlin.runCatching {
                Log.d("AppDebug", "updateUser: $user")
                user.fileId = fileId
                updateUser(user).perform()
            }.onSuccess {
                Log.d("AppDebug", "updateUser: $it")
                cont.resume(it)
            }.onFailure {
                cont.resumeWithException(it)
            }
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