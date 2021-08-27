package com.example.chatapplication.ui.main.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentChatsBinding
import com.example.chatapplication.ui.base.BaseChatsFragment
import com.example.chatapplication.ui.base.BaseFragment
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.example.chatapplication.ui.main.chats.mvi.ChatsViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class ChatsFragment : BaseChatsFragment<FragmentChatsBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        loadChats()
        subscribeToObservers()
        handleOnClickEvents()

    }

    private fun handleOnClickEvents() {
        binding.btnStartNewChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_newChatFragment)
        }

    }

    private fun subscribeToObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    viewModel.dataState.collect{
                        onStateChangeListener.onDataStateChanged(it)
                    }
                }

                launch {
                    viewModel.viewState.collect {
                        when(it){
                            is ChatsViewState.Chats -> {
                                Log.d("AppDebug", "subscribeToObservers: chat list updated ${it.chats}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadChats() {
        viewModel.setStateEvent(ChatsStateEvent.LoadAllChats)
    }
}