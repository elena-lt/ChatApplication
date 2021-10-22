package com.example.data.sessionManager.keyManager

import android.content.SharedPreferences
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.example.data.utils.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.UnrecoverableEntryException
import java.util.*
import javax.inject.Inject

class KeyManager @Inject constructor(
    private val encryptor: Encrypt,
    private val decrypt: Decrypt,
    private val sharedPreferencesEditor: SharedPreferences.Editor
) {

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun encryptData(userId: String, data: String): ByteArray {
//        return withContext(Dispatchers.IO){
        val byte = encryptor.encryptText(getAlias(userId), data)
//            try {
        sharedPreferencesEditor.putString(
            Const.SP_DATA,
            Base64.encodeToString(byte, Base64.DEFAULT)
        ).commit()

        sharedPreferencesEditor.putString(
            Const.SP_IV,
            Base64.encodeToString(encryptor.iv, Base64.DEFAULT)
        ).commit()
//                return@withContext byte
//            }catch (
//                e: Exception){
//                return@withContext null
//            }
        return byte
    }

    private fun getAlias(id: String): String {
        return "${Const.KEY_ALIAS}*$id"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun decryptData(userId: String, encryptedData: ByteArray, iv: ByteArray): String {
        return decrypt.decryptData(getAlias(userId), encryptedData, iv)
    }
}