package com.example.chatapplication.recyclerViewUtils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.ItemChatLayoutBinding
import com.example.chatapplication.databinding.ItemUserLayoutBinding
import com.example.chatapplication.models.Models

sealed class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class UserItemViewHolder(
        val binding: ItemUserLayoutBinding,
        private val onClickListener: OnClickListener?
    ) : RecyclerViewHolder(binding) {
        fun bind(user: Models.User) {
            binding.tvUserName.text = user.login
            binding.root.setOnClickListener {
                onClickListener?.onItemSelected(adapterPosition, user)
            }
        }
    }

    class ChatItemViewHolder(
        val binding: ItemChatLayoutBinding,
        private val onClickListener: OnClickListener?
    ): RecyclerViewHolder(binding){

        fun bind (chatItem: Models.Chat){
            binding.tvUserName.text = chatItem.name
            binding.tvLastMessage.text = chatItem.lastMessage
            binding.root.setOnClickListener {
                onClickListener?.onItemSelected(adapterPosition, chatItem)
            }
        }
    }
}

interface OnClickListener {
    fun onItemSelected(position: Int, item: Models)
}