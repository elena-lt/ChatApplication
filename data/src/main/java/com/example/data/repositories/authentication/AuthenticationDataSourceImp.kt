@file:Suppress("RemoveExplicitTypeArguments")

package com.example.data.repositories.authentication

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.sessionManager.SessionManger
import com.example.data.sessionManager.keyManager.KeyManager
import com.quickblox.chat.QBChatService
import com.quickblox.content.QBContent
import com.quickblox.users.QBUsers
import com.quickblox.users.QBUsers.updateUser
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthenticationDataSourceImp @Inject constructor(
    val sessionManager: SessionManger,
    val qbChatService: QBChatService,
    private val keyManager: KeyManager
) : AuthenticationDataSource {

    @RequiresApi(Build.VERSION_CODES.M)
    @ExperimentalCoroutinesApi
    override suspend fun signUpUser(
        username: String,
        email: String,
        fullName: String,
        password: String,
        profileImage: File?
    ): Flow<DataState<UserDomain>> = flow<DataState<UserDomain>> {
        val user = QBUser()
        user.login = username
        user.password = password
        user.email = email
        user.fullName = fullName

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

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun loginUser(login: String, password: String): Flow<DataState<UserDomain>> =
        flow {
            emit(DataState.LOADING(true))

            val user = QBUser(login, password)
            val query = QBUsers.signIn(user)
            kotlin.runCatching {
                query.perform()
            }.onSuccess {
                keyManager.encryptData(it.id.toString(), password)
                sessionManager.createChatServiceWithUser(user)
                emit(DataState.SUCCESS(UserMapper.toUserDomain(it)))
            }.onFailure {
                emit(DataState.ERROR<UserDomain>(errorMessage = it.toString()))
            }
        }
}