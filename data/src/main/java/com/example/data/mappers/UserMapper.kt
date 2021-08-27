package com.example.data.mappers

import com.example.core.models.UserDomain
import com.quickblox.users.model.QBUser

object UserMapper {

    fun toUserDomain(user: QBUser?): UserDomain {
        return UserDomain(
            id = user?.id,
            login = user?.login,
            fullName = user?.fullName,
            email = user?.email,
            externalId = user?.externalId
        )
    }
}