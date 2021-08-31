package com.example.chatapplication.models.mapper

import com.example.chatapplication.models.Models
import com.example.core.models.UserDomain

object UserMapper {

    fun toUser(userDomain: UserDomain): Models.User {
        return Models.User(
            userDomain.id,
            userDomain.login,
            userDomain.fullName,
            userDomain.email,
            userDomain.blobId,
            userDomain.externalId
        )
    }
}