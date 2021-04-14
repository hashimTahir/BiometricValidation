/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.core.content.ContextCompat
import com.hashim.biometriclogin.Constants.Companion.H_CIPHER_TEXT_KEY
import com.hashim.biometriclogin.Constants.Companion.H_SHARED_PREFS
import com.hashim.biometriclogin.crypto.CryptoManagerImpl
import com.hashim.biometriclogin.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var hActivityMainBinding: ActivityMainBinding
    private val hMainViewModel: MainViewModel by viewModels()
    private lateinit var hExecutor: Executor


    private val hCryptoManagerImpl = CryptoManagerImpl
    private val hCipherWrapper
        get() = hCryptoManagerImpl.hGetCipherFromSharedPrefsOrDataStore(
                context = applicationContext,
                filename = H_SHARED_PREFS,
                mode = Context.MODE_PRIVATE,
                prefKey = H_CIPHER_TEXT_KEY,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)

        Timber.d("ONcreate")
        hExecutor = ContextCompat.getMainExecutor(this)

        hCheckIfBioMeticAuthenticationisAvailable()
    }


    private fun hCheckIfBioMeticAuthenticationisAvailable() {
        Timber.d("Called")
        val hBioMetricManager = from(this)
        when (
            hBioMetricManager.canAuthenticate(
                    BIOMETRIC_STRONG or
                            DEVICE_CREDENTIAL or
                            BIOMETRIC_WEAK
            )
        ) {
            BIOMETRIC_SUCCESS ->
                hHandleSytemHasBioMetrics()
            BIOMETRIC_ERROR_NO_HARDWARE,
            BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                hActivityMainBinding.hBiometricLogin.visibility = View.GONE
                if (hCipherWrapper == null) {
                    hDoConvertionalLoginWithPassword()
                }
            }
        }

    }

    private fun hDoConvertionalLoginWithPassword() {

    }

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

    private fun hShowBiometricPrompter() {
        Timber.d("hShowBiometricPrompter")

    }

}