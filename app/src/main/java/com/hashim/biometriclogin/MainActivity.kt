/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.core.content.ContextCompat
import com.hashim.biometriclogin.MainViewModel
import com.hashim.biometriclogin.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var hActivityMainBinding: ActivityMainBinding
    private val hMainViewModel: MainViewModel by viewModels()
    private lateinit var executor: Executor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)

        Timber.d("ONcreate")
        executor = ContextCompat.getMainExecutor(this)

        hCheckIfBioMeticAuthenticationisAvailable()
    }


    private fun hCheckIfBioMeticAuthenticationisAvailable() {
        Timber.d("Called")
        val hBioMetricManager = from(this)
        when (hBioMetricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BIOMETRIC_WEAK)) {
            BIOMETRIC_SUCCESS ->
                Timber.d("App can authenticate using biometrics.")
            BIOMETRIC_ERROR_NO_HARDWARE ->
                Timber.d("No biometric features available on this device.")
            BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Timber.d("Biometric features are currently unavailable.")
            BIOMETRIC_ERROR_NONE_ENROLLED -> {

                Timber.d("error occured")
            }
        }

    }
}