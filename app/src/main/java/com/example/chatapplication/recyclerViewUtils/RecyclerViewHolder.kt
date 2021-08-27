package com.example.chatapplication.recyclerViewUtils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.ItemUserLayoutBinding
import com.example.chatapplication.models.Models

sealed class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    val onItemClickListener: ((view: View, item: Models, position: Int) -> Unit)? = null

    class UserItemViewHolder(
        val binding: ItemUserLayoutBinding,
        private val onClickListener: OnClickListener?
    ) : RecyclerViewHolder(binding) {
        fun bind(user: Models.User) {
            binding.tvUserName.text = user.login
            binding.root.setOnClickListener {
//                onItemClickListener?.invoke(it, user, adapterPosition)
                onClickListener?.onItemSelected(adapterPosition, user)
            }
        }
    }
}

interface OnClickListener {
    fun onItemSelected(position: Int, item: Models)
}