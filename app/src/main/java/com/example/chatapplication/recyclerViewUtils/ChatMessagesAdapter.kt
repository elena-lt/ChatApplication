package com.example.chatapplication.recyclerViewUtils

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.ItemChatMessageLeftBinding
import com.example.chatapplication.databinding.ItemChatMessageRightBinding
import com.example.chatapplication.models.ChatMessage
import com.example.chatapplication.recyclerViewUtils.MessagesViewHolder.ReceivedMessageViewHolder
import com.example.chatapplication.recyclerViewUtils.MessagesViewHolder.SentMessageViewHolder
import com.example.data.utils.Const
import com.quickblox.auth.session.QBSessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ChatMessagesAdapter (private val currUserId: Int): RecyclerView.Adapter<MessagesViewHolder>() {

    companion object {
        const val MSG_LEFT = 10
        const val MSG_RIGHT = 15
    }

    private val diffUtil = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem.body == newItem.body
    }

    val differ = AsyncListDiffer(this, diffUtil)

    fun submitList(list: List<ChatMessage>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return when (viewType) {
            MSG_LEFT -> {
                ReceivedMessageViewHolder(
                    ItemChatMessageLeftBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            MSG_RIGHT -> {
                SentMessageViewHolder(
                    ItemChatMessageRightBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("No such view holder found")
        }
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(differ.currentList[position])
            }
            is ReceivedMessageViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].senderId) {
            currUserId -> MSG_RIGHT

            else -> MSG_LEFT
        }
    }
}

sealed class MessagesViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class SentMessageViewHolder(val binding: ItemChatMessageRightBinding) :
        MessagesViewHolder(binding) {
        fun bind(chatMessage: ChatMessage) {
            binding.tvMessage.text = chatMessage.body
        }
    }

    class ReceivedMessageViewHolder(val binding: ItemChatMessageLeftBinding) :
        MessagesViewHolder(binding) {
        fun bind(chatMessage: ChatMessage) {
            binding.tvMessage.text = chatMessage.body
        }
    }
}