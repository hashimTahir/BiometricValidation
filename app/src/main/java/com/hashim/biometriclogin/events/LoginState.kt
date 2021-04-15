/*
 * Copyright (c) 2021/  4/ 16.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin.events

sealed class LoginState {

    data class FailedLoginState(
        val hUsernameError: String? = null,
        val hPasswordError: String? = null
    ) : LoginState()

    data class SuccessLoginState(
        val hIsDataValid: Boolean = false
    ) : LoginState()
}