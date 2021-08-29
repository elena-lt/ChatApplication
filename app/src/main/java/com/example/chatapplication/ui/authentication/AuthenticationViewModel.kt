package com.example.chatapplication.ui.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.ui.authentication.mvi.AuthenticationStateEvent
import com.example.chatapplication.ui.authentication.mvi.AuthenticationState
import com.example.core.models.UserDomain
import com.example.core.usecases.authentication.LoginUserUseCase
import com.example.core.usecases.authentication.SignUpUserUseCase
import com.example.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private var _authState = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
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

    fun handleIntent(intent: AuthenticationStateEvent) {
        viewModelScope.launch {
            when (intent) {
                is AuthenticationStateEvent.LoginUser -> {
                    loginUser(intent.login, intent.password)
                }
                is AuthenticationStateEvent.SignUpUser -> {
                    signUpUser(intent.username, intent.password)
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

    fun setDataState(dataState: DataState<UserDomain>) {
        viewModelScope.launch {
            _dataState.emit(dataState)
        }
    }

    private fun signUpUser(email: String, password: String) {
        viewModelScope.launch {
            signUp.execute(email, password).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            login.execute(username, password).collect { dataState ->
                setDataState(dataState)
            }
        }
    }
}
