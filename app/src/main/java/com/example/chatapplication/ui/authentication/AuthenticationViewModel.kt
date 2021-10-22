package com.example.chatapplication.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.ui.authentication.mvi.AuthenticationState
import com.example.chatapplication.ui.authentication.mvi.AuthenticationStateEvent
import com.example.chatapplication.utils.Constants
import com.example.core.models.UserDomain
import com.example.core.usecases.authentication.LoginUserUseCase
import com.example.core.usecases.authentication.SignUpUserUseCase
import com.example.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@InternalCoroutinesApi
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val signUp: SignUpUserUseCase,
    private val login: LoginUserUseCase
) : ViewModel() {
    private val _intent: MutableSharedFlow<AuthenticationStateEvent> = MutableSharedFlow()
    val intent = _intent.asSharedFlow()

    private val _dataState: MutableSharedFlow<DataState<UserDomain>> = MutableSharedFlow()
    val dataState = _dataState.asSharedFlow()

    private var _authState = MutableStateFlow<AuthenticationState>(AuthenticationState())
    val authState: StateFlow<AuthenticationState> get() = _authState.asStateFlow()

    val currentState: AuthenticationState
        get() = _authState.value

    init {
        subscribeIntents()
    }

    @InternalCoroutinesApi
    private fun subscribeIntents() {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    private fun handleIntent(intent: AuthenticationStateEvent) {
        viewModelScope.launch {
            when (intent) {
                is AuthenticationStateEvent.LoginUser -> {
                    loginUser(intent.login, intent.password)
                }
                is AuthenticationStateEvent.SignUpUser -> {
                    signUpUser(
                        intent.username,
                        intent.email,
                        intent.fullName,
                        intent.password,
                        intent.confirmPassword,
                        intent.image
                    )
                }
            }
        }
    }

    fun setIntent(intent: AuthenticationStateEvent) {
        val newIntent = intent
        viewModelScope.launch { _intent.emit(newIntent) }
    }

    fun setState(state: AuthenticationState) {
        _authState.value = state
    }

    private fun setDataState(dataState: DataState<UserDomain>) {
        viewModelScope.launch {
            _dataState.emit(dataState)
        }
    }

    private fun signUpUser(
        login: String,
        email: String,
        fullName: String,
        password: String,
        confirmPassword: String,
        image: File?
    ) {

        if (login.isBlank() || email.isBlank() || fullName.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return setState(
                AuthenticationState(
                    error = AuthenticationState.Error(errorMessage = Constants.FIELDS_NOT_FILLED_OUT_MSG)
                )
            )
        }

        if (password != confirmPassword) {
            return setState(
                AuthenticationState(
                    error = AuthenticationState.Error(errorMessage = Constants.PASSWORD_DOESNT_MATCH)
                )
            )
        }

        viewModelScope.launch {
            signUp.execute(login, email, fullName, password, image).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    private fun loginUser(username: String, password: String) {

        if (username.isBlank() || password.isBlank()) {
            return setState(
                AuthenticationState(
                    error = AuthenticationState.Error(errorMessage = Constants.FIELDS_NOT_FILLED_OUT_MSG)
                )
            )
        }

        viewModelScope.launch {
            login.execute(username, password).collect { dataState ->
                setDataState(dataState)
            }
        }
    }
}
