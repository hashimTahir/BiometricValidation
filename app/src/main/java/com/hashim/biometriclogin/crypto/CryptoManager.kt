/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin.crypto

import android.content.Context
import com.hashim.biometriclogin.data.CipherWrapper
import javax.crypto.Cipher

interface CryptoManager {

    fun hGetInitializedCipherForEncryption(
        keyName: String
    ): Cipher

    fun hGetInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher

    /**
     * The Cipher created with hGetInitializedCipherForEncryption is used here
     */
    fun hEncryptData(
        plaintext: String,
        cipher: Cipher
    ): CipherWrapper

    /**
     * The Cipher created with hGetInitializedCipherForDecryption is used here
     */
    fun hDecryptData(ciphertext: ByteArray, cipher: Cipher): String

    fun hSaveCipherToSharedPrefsOrDataStore(
        ciphertextWrapper: CipherWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    )

    fun hGetCipherFromSharedPrefsOrDataStore(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CipherWrapper?
}