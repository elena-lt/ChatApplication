package com.example.data.repositories.main.account

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.mappers.UserMapper
import com.example.data.persistance.AccountPropertiesDao
import com.example.data.persistance.entities.AccountPropertiesEntity
import com.example.data.repositories.NetworkBoundResource
import com.example.data.utils.ConnectivityManager
import com.example.data.utils.Const.SP_USER_ID
import com.example.data.utils.Const.TAG
import com.quickblox.content.QBContent
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AccountDataSourceImp @Inject constructor(
    val connectivityManager: ConnectivityManager,
    val accountPropertiesDao: AccountPropertiesDao,
    private val sharedPreferences: SharedPreferences
) : AccountDataSource {

    @ExperimentalCoroutinesApi
    override suspend fun updateProfileImage(image: File)=
          object : NetworkBoundResource<AccountPropertiesEntity, UserDomain>(connectivityManager) {
            val userId = sharedPreferences.getInt(SP_USER_ID, -1)

            override fun forceFetch(): Boolean = true

            override suspend fun loadFromDB(): DataState<UserDomain> {
                Log.d(TAG, "loadFromDB: loading data from db $userId")
                val user = accountPropertiesDao.searchByUserId(userId)
                return DataState.SUCCESS(
                    UserDomain(
                        user?.id,
                        user?.login,
                        user?.fullName,
                        user?.email,
                        user?.profileImg,
                        user?.externalId
                    )
                )
            }

            override suspend fun createCall(): DataState<AccountPropertiesEntity> {
                var accountProps: AccountPropertiesEntity? = null
                val user = getUserById(userId)

                if (user != null) {
                    Log.d(TAG, "createCall: user is not null")
                    kotlin.runCatching {
                        QBContent.uploadFileTask(image, false, null).perform()
                    }.onSuccess {
                        Log.d(TAG, "createCall: successfully uploaded file")
                        user.fileId = it.id
                        val uid = it.uid
                        val query = updateUser(user)
                        if (query != null) {
                            val imageBm = downloadFile(uid)
                            if (imageBm != null) {
                                val updatedUser =
                                    UserMapper.toUserDomain(query).copy(blobId = imageBm)
                                accountProps = AccountPropertiesEntity(
                                    updatedUser.id!!,
                                    updatedUser.login!!,
                                    updatedUser.fullName ?: "",
                                    updatedUser.email ?: "",
                                    updatedUser.blobId
                                )
                                saveFetchResult(accountProps)
                            } else {
                                onFetchFailed("image was not downloaded")
                            }
                        }
                    }.onFailure {
                        onFetchFailed(it.toString())
                    }
                }
                return DataState.SUCCESS(accountProps)
            }

            override suspend fun saveFetchResult(data: AccountPropertiesEntity?) {
                data?.let {
                    val accountPropertiesEntity = AccountPropertiesEntity(
                        it.id,
                        it.login,
                        it.email,
                        it.fullName,
                        it.profileImg,
                        it.externalId ?: ""
                    )
                    accountPropertiesDao.updateOrInsert(accountPropertiesEntity)
                }
            }
            override fun onFetchFailed(throwable: String) {
                Log.d(TAG, "onFetchFailed: something went wrong")
            }
        }.flow

//    override suspend fun updateProfileImage(image: File): Flow<DataState<UserDomain>> = flow<DataState<UserDomain>> {
//        emit(DataState.LOADING(true))
//
//        var updatedUser: UserDomain?
//
//        val userId = sharedPreferences.getInt(SP_USER_ID, -1)
//        val user = getUserById(userId)
//
//        if (user != null) {
//            kotlin.runCatching {
//                QBContent.uploadFileTask(image, false, null).perform()
//            }.onSuccess {
//                user.fileId = it.id
//                val uid = it.uid
//                val query = updateUser(user)
//                if (query != null) {
//                    val imageBm = downloadFile(uid)
//                    if (imageBm != null){
//                        updatedUser = UserMapper.toUserDomain(query).copy(blobId = imageBm)
//                        emit(DataState.SUCCESS(updatedUser))
//                    }else{
//                        emit(DataState.ERROR(UNKNOWN_ERROR))
//                    }
//                } else {
//                    emit(DataState.ERROR(UNKNOWN_ERROR))
//                }
//            }.onFailure {
//                emit(DataState.ERROR(it.message ?: UNKNOWN_ERROR))
//            }
//        }
//    }

    private suspend fun downloadFile(uid: String): Bitmap? {

        return suspendCoroutine { cont ->
            kotlin.runCatching {
                QBContent.downloadFile(uid).perform()
            }.onSuccess {
                Log.d(TAG, "downloadFile: success")
                cont.resume(BitmapFactory.decodeStream(it))
            }.onFailure {
                Log.d(TAG, "downloadFile: error $it")
                cont.resumeWithException(it)
            }
        }
    }

    private suspend fun updateUser(user: QBUser): QBUser? {
        return suspendCoroutine { cont ->
            kotlin.runCatching {
                QBUsers.updateUser(user).perform()
            }.onSuccess {
                Log.d(TAG, "updateUser: user updated")
                cont.resume(it)
            }.onFailure {
                Log.d(TAG, "updateUser: erroe updating user: ${it.toString()}")
                cont.resumeWithException(it)
            }
        }
    }

    private suspend fun getUserById(userId: Int): QBUser? {

        return suspendCoroutine { cont ->
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