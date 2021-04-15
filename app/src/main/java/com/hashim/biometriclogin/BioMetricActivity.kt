/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.hashim.biometriclogin.crypto.BioMetricUtis.H_HAS_BIOMETRIC_VALIDATION
import com.hashim.biometriclogin.Constants.Companion.H_BIOMETRIC_KEY
import com.hashim.biometriclogin.crypto.BioMetricUtis
import com.hashim.biometriclogin.crypto.CryptoManagerImpl
import com.hashim.biometriclogin.data.TestUser
import com.hashim.biometriclogin.databinding.ActivityBioMetricBinding
import timber.log.Timber
import java.util.*

class BioMetricActivity : AppCompatActivity() {
    lateinit var hActivityBioMetricBinding: ActivityBioMetricBinding
    private val hBioMetricUtis = BioMetricUtis
    private val hCryptoManagerImpl = CryptoManagerImpl

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityBioMetricBinding = ActivityBioMetricBinding.inflate(layoutInflater)
        setContentView(hActivityBioMetricBinding.root)

        hSetupListeners()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hSetupListeners() {
        hActivityBioMetricBinding.hCancelB.setOnClickListener {
            finish()
        }

        hActivityBioMetricBinding.hAuthorizeB.setOnClickListener {
            hLoginUser(hActivityBioMetricBinding.hUserNameTv.text.toString())
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hLoginUser(userName: String) {
        val hTestUser = hCreateFakeUser(userName)
        hBioMetricUtis.hCheckIfBioMeticAuthenticationisAvailable(this) {
            when (it) {
                H_HAS_BIOMETRIC_VALIDATION -> {
                    val hPrompt = hBioMetricUtis.hCreateBioMetricPrompt(this) {
                        hEncryptAndStoreToken(it, hTestUser)
                    }
                    val hPromptInfo = hBioMetricUtis.hCreatePromptInfo(this)

                    val hCipher = hCryptoManagerImpl.hGetInitializedCipherForEncryption(
                        H_BIOMETRIC_KEY
                    )
                    hPrompt.authenticate(hPromptInfo, BiometricPrompt.CryptoObject(hCipher))
                }
            }
        }
    }

    private fun hEncryptAndStoreToken(
        authResult: BiometricPrompt.AuthenticationResult,
        user: TestUser
    ) {
        authResult.cryptoObject?.cipher?.apply {

            Timber.d("Test token is $user")
            val encryptedServerTokenWrapper = hCryptoManagerImpl.hEncryptData(user.hToken!!, this)
            hCryptoManagerImpl.hSaveCipherToSharedPrefsOrDataStore(
                encryptedServerTokenWrapper,
                applicationContext,
                Constants.H_SHARED_PREFS,
                Context.MODE_PRIVATE,
                Constants.H_CIPHER_TEXT_KEY,
            )
        }
        finish()
    }


    private fun hCreateFakeUser(userName: String) = TestUser(
        hToken = UUID.randomUUID().toString(),
        hUserName = userName
    )
}