package com.example.chatapplication.ui.main.account.mvi

sealed class AccountStateEvent {

    object Logout: AccountStateEvent()
}