package com.example.data.mappers

import com.example.core.models.UserDomain
import com.example.data.persistance.entities.AccountPropertiesEntity
import com.quickblox.users.model.QBUser

object UserMapper {

    fun toUserDomain(user: QBUser?): UserDomain {
        return UserDomain(
            id = user?.id,
            login = user?.login,
            fullName = user?.fullName,
            email = user?.email,
            blobId = null,
            externalId = user?.externalId
        )
    }

    fun toUserDomain(accountProp: AccountPropertiesEntity): UserDomain {
        return UserDomain(
            accountProp.id,
            accountProp.login,
            accountProp.fullName,
            accountProp.email,
            accountProp.profileImg,
            accountProp.externalId
        )
    }
}