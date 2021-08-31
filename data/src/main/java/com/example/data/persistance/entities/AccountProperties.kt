package com.example.data.persistance.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_properties")
data class AccountProperties(

    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val login: String,
    @ColumnInfo (name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo (name = "profile_img")
    val profileImg: Bitmap? = null,
    @ColumnInfo (name = "external_id")
    val externalId: String? = null,

    )
