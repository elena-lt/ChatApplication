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
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentNewChatBinding
import com.example.chatapplication.models.Models
import com.example.chatapplication.recyclerViewUtils.OnClickListener
import com.example.chatapplication.recyclerViewUtils.RecyclerViewAdapter
import com.example.chatapplication.ui.base.BaseChatsFragment
import com.example.chatapplication.ui.main.chats.mvi.ChatsStateEvent
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class NewChatFragment : BaseChatsFragment<FragmentNewChatBinding>(), OnClickListener {

    private lateinit var usersRvAdapter: RecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUser()
        setupRecyclerView()
        subscribeToObservers()
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
                        viewState.users?.users?.let{
                            usersRvAdapter.submitList(it)
                        }

                        viewState.openChatDialog?.let {
                            Log.d("AppDebug", "subscribeToObservers: ${it.chatDialog}")
                            findNavController().navigate(R.id.action_newChatFragment_to_activeChatFragment)
                        }
                    }
                }
            }
        }
    }

    private fun fetchUser() {
        viewModel.setStateEvent(ChatsStateEvent.FindUser)
    }

    private fun startPrivateChat(userId: Int){
        viewModel.setStateEvent(ChatsStateEvent.StartNewChat(userId))
    }

    private fun setupRecyclerView() {
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@NewChatFragment.context)
            usersRvAdapter = RecyclerViewAdapter(this@NewChatFragment)
            adapter = usersRvAdapter
        }
    }

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNewChatBinding::inflate

    override fun onItemSelected(position: Int, item: Models) {
        val user = item as Models.User
        startPrivateChat(user.id)
    }

}