package com.example.data.repositories.main.account

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.utils.Const.SP_USER_ID
import com.example.data.utils.Const.TAG
import com.example.data.utils.Const.UNKNOWN_ERROR
import com.quickblox.content.QBContent
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AccountDataSourceImp @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AccountDataSource {

    override suspend fun updateProfileImage(image: File): Flow<DataState<UserDomain>> = flow<DataState<UserDomain>> {
        emit(DataState.LOADING(true))

        var updatedUser: UserDomain?

        val userId = sharedPreferences.getInt(SP_USER_ID, -1)
        val user = getUserById(userId)

        if (user != null) {
            kotlin.runCatching {
                QBContent.uploadFileTask(image, false, null).perform()
            }.onSuccess {
                user.fileId = it.id
                val uid = it.uid
                val query = updateUser(user)
                if (query != null) {
                    val imageBm = downloadFile(uid)
                    if (imageBm != null){
                        updatedUser = UserMapper.toUserDomain(query).copy(blobId = imageBm)
                        emit(DataState.SUCCESS(updatedUser))
                    }else{
                        emit(DataState.ERROR(UNKNOWN_ERROR))
                    }
                } else {
                    emit(DataState.ERROR(UNKNOWN_ERROR))
                }
            }.onFailure {
                emit(DataState.ERROR(it.message ?: UNKNOWN_ERROR))
            }
        }
    }

    private suspend fun downloadFile(uid: String) : Bitmap? {

        return suspendCoroutine {cont ->
            kotlin.runCatching {
                QBContent.downloadFile(uid).perform()
            }.onSuccess {
                cont.resume(BitmapFactory.decodeStream(it))
            }.onFailure {
                cont.resumeWithException(it)
            }
        }
    }

    private suspend fun updateUser(user: QBUser): QBUser? {
        return suspendCoroutine { cont ->
            kotlin.runCatching {
                QBUsers.updateUser(user).perform()
            }.onSuccess {
                cont.resume(it)
            }.onFailure {
                cont.resumeWithException(it)
            }
        }
    }

    private suspend fun getUserById(userId: Int): QBUser? {

        return suspendCoroutine {cont ->
            kotlin.runCatching {
                QBUsers.getUser(userId).perform()
            }.onSuccess {
                Log.d(TAG, "getUserById: success ${it.login}")
                cont.resume(it)
            }.onFailure {
                Log.d(TAG, "getUserById: error ${it.message}")
                cont.resumeWithException(it)
            }
        }
    }

    override suspend fun logout(): Flow<DataState<String>> = flow {
        emit(DataState.LOADING(true))

        kotlin.runCatching {
            QBUsers.signOut().perform()
        }.onSuccess {
            emit(DataState.SUCCESS("LOGGED_OUT"))
        }.onFailure {
            Log.d("AppDebug", "logout: $it")
        }
    }
}