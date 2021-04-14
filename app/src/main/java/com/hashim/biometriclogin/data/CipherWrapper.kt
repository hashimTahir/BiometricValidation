/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin.data

data class CipherWrapper(
    val ciphertext: ByteArray,
    val initializationVector: ByteArray
)
