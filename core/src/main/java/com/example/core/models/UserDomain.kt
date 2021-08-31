package com.example.core.models

import android.graphics.Bitmap

data class UserDomain(
    val id: Int? = null,
    val login: String? = null,
    val fullName: String? = null,
    val email: String?= null,
    val blobId: Bitmap? = null,
    val externalId: String? = null,

    )