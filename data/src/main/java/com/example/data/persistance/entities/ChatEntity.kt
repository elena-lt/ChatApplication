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
    val lastMessage: String? = null,

    @ColumnInfo(name = "last_message_date_sent")
    val lastMessageDateSent: Long = 0,

//    @ColumnInfo(name = "last_message_user_id")
//    val lastMessageUserId: Int,

//    @ColumnInfo(name = "photo")
//    val photo: String? = null,

//    @ColumnInfo(name = "user_id")
//    val userId: Int? = null,

//    @ColumnInfo(name = "room_jid")
//    val roomJid: String? = null,

    @ColumnInfo(name = "unread_message_count")
    val unreadMessageCount: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "occupant1")
    val occupant1: Int = -1,

    @ColumnInfo(name = "occupant2")
    val occupant2: Int = -1,

//    @ColumnInfo(name = "msg_recipient")
//    val msg_recipient: Int,

//    @ColumnInfo(name = "type")
//    val type: Int? = null,
)