/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.hashim.biometriclogin.Constants.Companion.H_BIOMETRIC_KEY
import com.hashim.biometriclogin.Constants.Companion.H_CIPHER_TEXT_KEY
import com.hashim.biometriclogin.Constants.Companion.H_SHARED_PREFS
import com.hashim.biometriclogin.crypto.BioMetricUtis
import com.hashim.biometriclogin.crypto.BioMetricUtis.H_ERROR_BIOMETRIC_VALIDATION
import com.hashim.biometriclogin.crypto.BioMetricUtis.H_HAS_BIOMETRIC_VALIDATION
import com.hashim.biometriclogin.crypto.CryptoManagerImpl
import com.hashim.biometriclogin.data.TestUser
import com.hashim.biometriclogin.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var hActivityMainBinding: ActivityMainBinding
    private val hMainViewModel: MainViewModel by viewModels()

    private val hBioMetricUtis = BioMetricUtis
    private val hCryptoManagerImpl = CryptoManagerImpl
    private val hCipherWrapper
        get() = hCryptoManagerImpl.hGetCipherFromSharedPrefsOrDataStore(
            context = applicationContext,
            filename = H_SHARED_PREFS,
            mode = Context.MODE_PRIVATE,
            prefKey = H_CIPHER_TEXT_KEY,
        )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)


        hMainViewModel.hCheckIfBiometricAuthenticationIsAvailable()

        hSetupListeners()

        hSubscribeObservers()

        hBioMetricUtis.hCheckIfBioMeticAuthenticationisAvailable(this) {
            when (it) {
                H_HAS_BIOMETRIC_VALIDATION -> {
                    hHandleSytemHasBioMetrics()
                }
                H_ERROR_BIOMETRIC_VALIDATION -> {
                    hActivityMainBinding.hBiometricLogin.visibility = View.GONE
                    if (hCipherWrapper == null) {
                        hDoConvertionalLoginWithPassword()
                    }
                }
            }
        }
    }

    private fun hSubscribeObservers() {

        hMainViewModel.hLoginResultLd.observe(this) { loginResult ->

        }
        hMainViewModel.hLoginStateLd.observe(this) { loginState ->

        }
    }

    private fun hSetupListeners() {
        hActivityMainBinding.hLoginButton.setOnClickListener {
            hMainViewModel.hDoConventionalLogin()
        }
    }


    private fun hDoConvertionalLoginWithPassword() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hHandleSytemHasBioMetrics() {

        Timber.d("hHandleSytemHasBioMetrics")
        hActivityMainBinding.hBiometricLogin.visibility = View.VISIBLE
        hActivityMainBinding.hBiometricLogin.setOnClickListener {

            /*Means app has biometrics already enabled*/
            if (hCipherWrapper != null) {
                hShowBiometricPrompter()
            } else {
                /*Enable Biometric login for the app*/
                startActivity(Intent(this, BioMetricActivity::class.java))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hShowBiometricPrompter() {
        hCipherWrapper?.let { cipherWrapper ->
            val hCipher = hCryptoManagerImpl.hGetInitializedCipherForDecryption(
                keyName = H_BIOMETRIC_KEY,
                initializationVector = hCipherWrapper!!.initializationVector
            )
            val hPrompt = hBioMetricUtis.hCreateBioMetricPrompt(this) {
                hDecryptToken(it)
            }
            val hPromptInfo = hBioMetricUtis.hCreatePromptInfo(this)
            hPrompt.authenticate(hPromptInfo, BiometricPrompt.CryptoObject(hCipher))

        }
    }

    private fun hDecryptToken(authResult: BiometricPrompt.AuthenticationResult) {
        hCipherWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val hText =
                    hCryptoManagerImpl.hDecryptData(
                        textWrapper.ciphertext,
                        it
                    )
                val h = TestUser(hToken = hText)


                Timber.d("User $h")
            }
        }
    }

}

