/*
 * Copyright (c) 2021/  4/ 16.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin.events

import javax.crypto.Cipher

data class BioMetricResult(
    val hMessage: String? = null,
    val hHasBioMetricValidation: Boolean = false,
    val hCreatePompter: Boolean = false,
    val hCipher: Cipher? = null,
    val hOpenBioMetricEnablerIntnet: Boolean = false,

    )