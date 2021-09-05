package com.example.core.usecases.authentication

import android.util.Log
import com.example.core.models.UserDomain
import com.example.core.repositories.AuthenticationRepository
import com.example.core.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class SignUpUserUseCase @Inject constructor(private val authRepository: AuthenticationRepository) {

    suspend fun execute(
        username: String,
        email: String,
        fullName: String,
        password: String,
        profileImage: File?
    ): Flow<DataState<UserDomain>> {
        Log.d("AppDebug", "SignUpUserUseCase")
        return authRepository.signUpUser(username, email, fullName, password, profileImage)
            .flowOn(Dispatchers.IO)
    }
}