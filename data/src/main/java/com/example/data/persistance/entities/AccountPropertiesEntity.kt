package com.example.data.persistance.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_properties")
data class AccountPropertiesEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val login: String,
    @ColumnInfo (name = "full_name")
    val fullName: String? = null,
    @ColumnInfo(name = "email")
    val email: String? = null,
    @ColumnInfo (name = "profile_img")
    var profileImg: Bitmap? = null,
    @ColumnInfo (name = "external_id")
    val externalId: String? = null,

    )
