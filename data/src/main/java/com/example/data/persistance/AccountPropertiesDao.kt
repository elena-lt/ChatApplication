package com.example.data.persistance

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.persistance.entities.AccountPropertiesEntity

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountProperties(accountProperties: AccountPropertiesEntity)

    @Query("SELECT * FROM account_properties WHERE id =:userId")
    suspend fun searchByUserId(userId: Int): AccountPropertiesEntity?

    @Query(
        """
            UPDATE account_properties 
            SET login =:login,  
            email = :email, 
            full_name = :fullName, 
            profile_img =:profileImg, 
            external_id =:externalId
            WHERE id=:id
        """
    )
    suspend fun updateAccountProperties(
        id: Int,
        login: String,
        email: String,
        fullName: String,
        profileImg: Bitmap?,
        externalId: String
    )

    suspend fun updateOrInsert(accountProp: AccountPropertiesEntity) {
        val account = searchByUserId(accountProp.id)
        if (account ==null) insertAccountProperties(accountProp)
        else updateAccountProperties(
            accountProp.id,
            accountProp.login,
            accountProp.email,
            accountProp.fullName,
            accountProp.profileImg,
            accountProp.externalId ?: ""
        )
    }
}