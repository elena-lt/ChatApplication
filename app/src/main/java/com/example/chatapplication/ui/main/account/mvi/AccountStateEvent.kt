package com.example.chatapplication.ui.main.account.mvi

import java.io.File

sealed class AccountStateEvent {

    object Logout: AccountStateEvent()
    object LoadAccountProperties: AccountStateEvent()
    data class UpdateAccountProperties(val image: File): AccountStateEvent()
}