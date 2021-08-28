package com.example.chatapplication.ui.main.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentActiveChatBinding
import com.example.chatapplication.ui.base.BaseChatsFragment
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ActiveChatFragment : BaseChatsFragment<FragmentActiveChatBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentActiveChatBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendMessage(){

    }


}