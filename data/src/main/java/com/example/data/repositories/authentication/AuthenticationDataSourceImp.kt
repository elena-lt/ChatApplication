package com.example.data.repositories.authentication

import android.os.Bundle
import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.repositories.authentication.AuthenticationDataSource
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AuthenticationDataSourceImp : AuthenticationDataSource {

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
            Log.d("AppDebug", "loginUser: ${Thread.currentThread().name}")
            emit(DataState.LOADING(true))

            val user = QBUser(login, password)
            val query = QBUsers.signIn(user)
            kotlin.runCatching {
                query.perform()
            }.onSuccess {
                emit(DataState.SUCCESS(UserMapper.toUserDomain(it)))
            }.onFailure {
                emit(DataState.ERROR<UserDomain>(errorMessage = it.toString()))
            }

        }

}