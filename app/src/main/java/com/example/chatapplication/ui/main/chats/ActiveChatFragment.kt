package com.example.chatapplication.ui.main.chats

import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import com.example.chatapplication.models.ChatMessage
import com.example.chatapplication.recyclerViewUtils.ChatMessagesAdapter
import com.example.chatapplication.ui.base.BaseChatsFragment
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import com.example.data.utils.Const
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class ActiveChatFragment : BaseChatsFragment<FragmentActiveChatBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentActiveChatBinding::inflate

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var messagesRvAdapter: ChatMessagesAdapter
    private var messageList = mutableListOf<ChatMessage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserData()
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
                                messageList = it
                                submitDataToRv(messageList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun submitDataToRv(messageList: MutableList<ChatMessage>){
        messagesRvAdapter.submitList(messageList.asReversed())
        messagesRvAdapter.notifyDataSetChanged()
    }

    private fun setupUserData() {
        Log.d("AppDebug", "setupUserData: ${viewModel.currentState.toString()}")
        viewModel.currentState.openChatDialog?.chatDialog?.let {
            binding.tvMsgReceiverName.text = it.name
        }
    }

    private fun retrieveChatHistory() {
        viewModel.setStateEvent(
            ChatsStateEvent.RetrieveChatHistory(viewModel.currentState.openChatDialog?.chatDialog?.dialogId!!)
        )
    }

    private fun sendMessage(messageContent: String, chatId: String, occupantsIds: List<Int>) {
        messageList.add(
            ChatMessage(
                body = messageContent,
                senderId = sharedPreferences.getInt(Const.SP_USER_ID, -1)
            )
        )
        messagesRvAdapter.notifyDataSetChanged()
        viewModel.setStateEvent(ChatsStateEvent.SendMessage(messageContent, chatId, occupantsIds))
    }

    private fun setupRvAdapter() {
        binding.rvMessages.apply {
            val manager = LinearLayoutManager(this@ActiveChatFragment.context, LinearLayoutManager.VERTICAL, true)
            manager.stackFromEnd
            layoutManager = manager
            messagesRvAdapter = ChatMessagesAdapter(sharedPreferences.getInt(Const.SP_USER_ID, -1))
            adapter = messagesRvAdapter
        }
    }
}
