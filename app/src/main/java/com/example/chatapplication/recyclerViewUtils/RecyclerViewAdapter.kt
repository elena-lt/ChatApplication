package com.example.chatapplication.recyclerViewUtils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ItemChatLayoutBinding
import com.example.chatapplication.databinding.ItemUserLayoutBinding
import com.example.chatapplication.models.Models
import java.lang.IllegalArgumentException

class RecyclerViewAdapter(
    private val onClickListener: OnClickListener? = null,
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    companion object {
        const val USER = 0
        const val CHAT = 1
    }
//    val onItemClickListener: ((position: Int, item: Models) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<Models>() {
        override fun areItemsTheSame(oldItem: Models, newItem: Models) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Models, newItem: Models) =
            oldItem.hashCode() == newItem.hashCode()
    }

    val differ = AsyncListDiffer(this, diffUtil)
    fun submitList(list: List<Models>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return when (viewType) {
            USER -> {
                RecyclerViewHolder.UserItemViewHolder(
                    ItemUserLayoutBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ),
                    onClickListener = onClickListener
                )
            }
            CHAT -> {
                RecyclerViewHolder.ChatItemViewHolder(
                    ItemChatLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClickListener = onClickListener
                )
            }
            else -> throw IllegalArgumentException("No such view holder found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        when (holder) {
            is RecyclerViewHolder.UserItemViewHolder -> {
                holder.bind(differ.currentList[position] as Models.User)
            }
            is RecyclerViewHolder.ChatItemViewHolder -> {
                holder.bind(differ.currentList[position] as Models.Chat)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is Models.User -> USER
            is Models.Chat -> CHAT
        }
    }
}