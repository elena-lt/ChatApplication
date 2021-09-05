package com.example.chatapplication.ui.main.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.FragmentActiveChatBinding
import com.example.chatapplication.recyclerViewUtils.ChatMessagesAdapter
import com.example.chatapplication.ui.base.BaseChatsFragment
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBIncomingMessagesManager
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.exception.QBChatException
import com.quickblox.chat.listeners.QBChatDialogMessageListener
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@InternalCoroutinesApi
class ActiveChatFragment : BaseChatsFragment<FragmentActiveChatBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentActiveChatBinding::inflate

    private lateinit var messagesRvAdapter: ChatMessagesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveChatHistory()
        setupRvAdapter()
        handleOnClickEvents()
        subscribeToObservers()

    }

    private fun handleOnClickEvents() {
        binding.sendMessageBtn.setOnClickListener {
            sendMessage(
                binding.msgContentEdt.text.toString(),
                viewModel.currentState.openChatDialog?.chatDialog?.dialogId!!,
                viewModel.currentState.openChatDialog!!.chatDialog.occupantsIds!!
            )
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect { dataState ->
                        onStateChangeListener.onDataStateChanged(dataState)
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        viewState.openChatDialog?.let { chatHistory ->
                            chatHistory.messages?.let {
                                messagesRvAdapter.submitList(it)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun retrieveChatHistory() {
        viewModel.setStateEvent(
            ChatsStateEvent.RetrieveChatHistory(viewModel.currentState.openChatDialog?.chatDialog?.dialogId!!)
        )
    }

    private fun sendMessage(messageContent: String, chatId: String, occupantsIds: List<Int>) {
        viewModel.setStateEvent(ChatsStateEvent.SendMessage(messageContent, chatId, occupantsIds))
    }

    private fun setupRvAdapter() {
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ActiveChatFragment.context)
            messagesRvAdapter = ChatMessagesAdapter()
            adapter = messagesRvAdapter
        }
    }
}
