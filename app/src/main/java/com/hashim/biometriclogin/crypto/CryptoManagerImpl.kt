/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin.crypto

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.hashim.biometriclogin.data.CipherWrapper
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoManagerImpl : CryptoManager {


    private val KEY_SIZE = 256
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

    @RequiresApi(Build.VERSION_CODES.M)
    override fun hGetInitializedCipherForEncryption(
        keyName: String
    ): Cipher {
        val hCipher = hGetCipher()
        val hSecretKey = hGetOrCreateSecretKey(keyName)
        hCipher.init(Cipher.ENCRYPT_MODE, hSecretKey)
        return hCipher
    }

    override fun hGetInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher {
        val hCipher = hGetCipher()
        val hSecretKey = hGetOrCreateSecretKey(keyName)
        hCipher.init(Cipher.DECRYPT_MODE, hSecretKey, GCMParameterSpec(128, initializationVector))
        return hCipher
    }

    override fun hEncryptData(
        plaintext: String,
        cipher: Cipher
    ): CipherWrapper {
        val hCipherText = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return CipherWrapper(hCipherText, cipher.iv)
    }

    override fun hDecryptData(
        ciphertext: ByteArray,
        cipher: Cipher
    ): String {
        val hPlaintext = cipher.doFinal(ciphertext)
        return String(hPlaintext, Charset.forName("UTF-8"))
    }

    override fun hSaveCipherToSharedPrefsOrDataStore(
        ciphertextWrapper: CipherWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ) {
        TODO("Not yet implemented")
    }

    override fun hGetCipherFromSharedPrefsOrDataStore(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CipherWrapper? {
        TODO("Not yet implemented")
    }


    private fun hGetCipher(): Cipher {
        val hTransformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(hTransformation)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hGetOrCreateSecretKey(
        keyName: String
    ): SecretKey {
        // If Secretkey is already created for that keyName, return it.
        val hKeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)

        hKeyStore.load(null) // Keystore must be loaded before it can be accessed
        hKeyStore.getKey(keyName, null)?.let {
            return it as SecretKey
        }

        // if no key is available, create a new one
        val hParamsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        hParamsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = hParamsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

}