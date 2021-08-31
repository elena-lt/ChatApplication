package com.example.chatapplication.ui.main.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Dao
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentChatsBinding
import com.example.chatapplication.models.Models
import com.example.chatapplication.recyclerViewUtils.OnClickListener
import com.example.chatapplication.recyclerViewUtils.RecyclerViewAdapter
import com.example.chatapplication.ui.base.BaseChatsFragment
import com.example.chatapplication.ui.base.BaseFragment
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.example.chatapplication.ui.main.chats.mvi.ChatsViewState
import com.example.data.persistance.ChatsDao
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBIncomingMessagesManager
import com.quickblox.chat.exception.QBChatException
import com.quickblox.chat.listeners.QBChatDialogMessageListener
import com.quickblox.chat.model.QBChatMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class ChatsFragment : BaseChatsFragment<FragmentChatsBinding>(), OnClickListener {

    @Inject
    lateinit var chatDao: ChatsDao

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatsBinding::inflate

    private lateinit var chatsRvAdapter: RecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadChats()
        setupRecycler()
        subscribeToObservers()
        handleOnClickEvents()

    }

    override fun onResume() {

        super.onResume()
    }

    override fun onPause() {

        super.onPause()
    }

    private fun handleOnClickEvents() {
        binding.btnStartNewChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_newChatFragment)
        }

        binding.account.setOnClickListener {
            findNavController().navigate(R.id.accountFragment)
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect {
                        onStateChangeListener.onDataStateChanged(it)
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        viewState.chats?.let { chats ->
                            chatsRvAdapter.submitList(chats.chats)
                        }
                    }
                }
            }
        }
    }

    private fun loadChats() {
        viewModel.setStateEvent(ChatsStateEvent.LoadAllChats)
    }

    private fun setupRecycler() {
        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsFragment.context)
            chatsRvAdapter = RecyclerViewAdapter(this@ChatsFragment)
            adapter = chatsRvAdapter

        }
    }

    override fun onItemSelected(position: Int, item: Models) {
        val chat = item as Models.Chat
        viewModel.setViewState(
            viewModel.currentState.copy(openChatDialog = ChatsViewState.OpenChatDialog(chat, null))
        )
        findNavController().navigate(R.id.action_chatsFragment_to_activeChatFragment)
    }
}