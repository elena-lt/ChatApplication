package com.example.chatapplication.ui.main.account.mvi

import com.example.chatapplication.models.Models
import com.example.core.models.UserDomain
import java.io.File

sealed class AccountStateEvent {

    object Logout: AccountStateEvent()
    object LoadAccountProperties: AccountStateEvent()
    data class UpdateAccountProperties(val image: File): AccountStateEvent()
}