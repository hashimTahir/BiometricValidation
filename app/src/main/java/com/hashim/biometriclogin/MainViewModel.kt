/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hashim.biometriclogin.Constants.Companion.H_BIOMETRIC_KEY
import com.hashim.biometriclogin.crypto.BioMetricUtis
import com.hashim.biometriclogin.crypto.CryptoManagerImpl
import com.hashim.biometriclogin.data.TestUser
import com.hashim.biometriclogin.events.BioMetricResult
import com.hashim.biometriclogin.events.LoginResult
import com.hashim.biometriclogin.events.LoginState
import com.hashim.biometriclogin.events.LoginState.FailedLoginState
import com.hashim.biometriclogin.events.LoginState.SuccessLoginState
import timber.log.Timber
import java.util.*

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val hBioMetricUtis = BioMetricUtis
    private val hContext = application

    private val hLoginStateMld = MutableLiveData<LoginState>()
    val hLoginStateLd: LiveData<LoginState> = hLoginStateMld

    private val hLoginResultMld = MutableLiveData<LoginResult>()
    val hLoginResultLd: LiveData<LoginResult> = hLoginResultMld

    private val hBioMetricResultMld = MutableLiveData<BioMetricResult>()
    val hBioMetricResultLd: LiveData<BioMetricResult> = hBioMetricResultMld

    private val hCryptoManagerImpl = CryptoManagerImpl
    private val hCipherWrapper
        get() = hCryptoManagerImpl.hGetCipherFromSharedPrefsOrDataStore(
            context = hContext,
            filename = Constants.H_SHARED_PREFS,
            mode = Context.MODE_PRIVATE,
            prefKey = Constants.H_CIPHER_TEXT_KEY,
        )


    fun hCheckIfBiometricAuthenticationIsAvailable() {
        hBioMetricUtis.hCheckIfBioMeticAuthenticationisAvailable(hContext) {
            when (it) {
                BioMetricUtis.H_HAS_BIOMETRIC_VALIDATION -> {
                    hBioMetricResultMld.value = BioMetricResult(
                        hHasBioMetricValidation = true,
                    )
                }
                BioMetricUtis.H_ERROR_BIOMETRIC_VALIDATION -> {
                    hBioMetricResultMld.value = BioMetricResult(
                        hMessage = "Please login with password",
                    )
                }
            }
        }
    }

    fun hDoConventionalLogin(userName: String, password: String) {
        if (!hIsUserNameValid(userName)) {
            hLoginStateMld.value = FailedLoginState(
                hUsernameError = "Invalid Username"
            )
        } else if (!hIsPasswordValid(password)) {
            hLoginStateMld.value = FailedLoginState(
                hPasswordError = "Invalid password"
            )
        } else {
            hLoginStateMld.value = SuccessLoginState(
                hIsDataValid = true
            )
        }



        if (hIsUserNameValid(userName) && hIsPasswordValid(password)) {
            TestUser.hUserName = userName
            TestUser.hToken = UUID.randomUUID().toString()
            hLoginResultMld.value = LoginResult(true)
        } else {
            hLoginResultMld.value = LoginResult(false)
        }

    }

    private fun hIsUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun hIsPasswordValid(password: String): Boolean {
        return password.length > 5
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun hLoginWithBioMetricAuthentication() {

        if (hCipherWrapper != null) {
            hCreateBioMetricPrompter()
        } else {
            /*Enable Biometric login for the app*/
            hCreateEnableBioMetricForAppIntent()
        }
    }

    private fun hCreateEnableBioMetricForAppIntent() {
        Timber.d("Create Intent")
        hBioMetricResultMld.value = BioMetricResult(
            hOpenBioMetricEnablerIntnet = true
        )

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hCreateBioMetricPrompter() {
        hCipherWrapper?.let { cipherWrapper ->
            val hCipher = hCryptoManagerImpl.hGetInitializedCipherForDecryption(
                keyName = Constants.H_BIOMETRIC_KEY,
                initializationVector = hCipherWrapper!!.initializationVector
            )
            hBioMetricResultMld.value = BioMetricResult(
                hCreatePompter = true,
                hCipher = hCipher,
            )


        }
    }

    fun hDecryptToken(authResult: BiometricPrompt.AuthenticationResult) {
        Timber.d("Decrypt Token called")
        hCipherWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val hText =
                    hCryptoManagerImpl.hDecryptData(
                        textWrapper.ciphertext,
                        it
                    )
                TestUser.hToken = hText
            }
        }
    }

    fun hEncryptAndStoreToken(authResult: BiometricPrompt.AuthenticationResult) {

        authResult.cryptoObject?.cipher?.apply {

            val encryptedServerTokenWrapper = hCryptoManagerImpl.hEncryptData(TestUser.hToken!!, this)
            hCryptoManagerImpl.hSaveCipherToSharedPrefsOrDataStore(
                encryptedServerTokenWrapper,
                hContext,
                Constants.H_SHARED_PREFS,
                Context.MODE_PRIVATE,
                Constants.H_CIPHER_TEXT_KEY,
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hCreateCompletlyNewPrompter() {
            val hCipher = hCryptoManagerImpl.hGetInitializedCipherForEncryption(
                keyName = H_BIOMETRIC_KEY,
            )
            hBioMetricResultMld.value = BioMetricResult(
                hCreatePompter = true,
                hCipher = hCipher,
            )



    }
}