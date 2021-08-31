package com.example.chatapplication.ui.main.account

import androidx.lifecycle.viewModelScope
import com.example.chatapplication.models.mapper.UserMapper
import com.example.chatapplication.ui.base.BaseViewModel
import com.example.chatapplication.ui.main.account.mvi.AccountStateEvent
import com.example.chatapplication.ui.main.account.mvi.AccountViewState
import com.example.core.usecases.main.account.LogoutUseCase
import com.example.core.usecases.main.account.UpdateProfileImageUseCase
import com.example.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
    @Inject constructor(
        private val changeProfileImg: UpdateProfileImageUseCase,
        private val logout: LogoutUseCase
    ): BaseViewModel<AccountViewState, AccountStateEvent, DataState<*>>() {

    override fun createInitialState(): AccountViewState = AccountViewState()

    override fun handleStateEvent(stateEvent: AccountStateEvent) {
        when (stateEvent){
            is AccountStateEvent.Logout -> logout()
        }
    }

    fun changeProfileImage(image: File){
        viewModelScope.launch {
            changeProfileImg.invoke(image).collect { dataState ->
                setDataState(dataState)

                dataState.data?.let {
                    setViewState(currentState.copy(user = UserMapper.toUser(it), false))
                }
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            logout.invoke().collect {dataState ->
                setDataState(dataState)

                dataState.data?.let {
                    if (it == "LOGGED_OUT"){
                        setViewState(AccountViewState(null, true))
                    }
                }
            }
        }
    }
}