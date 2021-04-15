/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

sealed class LoginState {

    data class FailedLoginState(
        val hUsernameError: Int? = null,
        val hPasswordError: Int? = null
    ) : LoginState()

    data class SuccessLoginState(
        val hIsDataValid: Boolean = false
    ) : LoginState()
}