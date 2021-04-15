/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    fun hCheckIfBiometricAuthenticationIsAvailable() {


    }

    fun hDoConventionalLogin() {


    }

    private val hLoginStateMld = MutableLiveData<LoginState>()
    val hLoginStateLd: LiveData<LoginState> = hLoginStateMld

    private val hLoginResultMld = MutableLiveData<LoginResult>()
    val hLoginResultLd: LiveData<LoginResult> = hLoginResultMld

}