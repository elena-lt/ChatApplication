package com.example.data.persistance.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "dialog_id")
    var dialogId: String,

    @ColumnInfo(name = "last_message")
    val lastMessage: String,

//    @ColumnInfo(name = "last_message_date_sent")
//    val lastMessageDateSent: Long = 0,
//
//    @ColumnInfo(name = "last_message_user_id")
//    val lastMessageUserId: Int,
//
//    @ColumnInfo(name = "photo")
//    val photo: String? = null,
//
//    @ColumnInfo(name = "user_id")
//    val userId: Int? = null,
//
//    @ColumnInfo(name = "room_jid")
//    val roomJid: String? = null,
//
//    @ColumnInfo(name = "unread_message_count")
//    val unreadMessageCount: Int? = null,
//
//    @ColumnInfo(name = "name")
//    val name: String? = null,
//
//    @ColumnInfo(name = "unread_message_count")
//    val occupantsIds: List<Int>? = null,
//
//    @ColumnInfo(name = "type")
//    val type: Int? = null,
)