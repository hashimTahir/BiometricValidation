/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber

object BioMetricUtis {
    const val H_HAS_BIOMETRIC_VALIDATION = 0
    const val H_ERROR_BIOMETRIC_VALIDATION = 1
    fun hCheckIfBioMeticAuthenticationisAvailable(
        hContext: Context,
        hCallback: (Int) -> Unit
    ) {
        val hBioMetricManager = BiometricManager.from(hContext)
        when (
            hBioMetricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
        ) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                hCallback(H_HAS_BIOMETRIC_VALIDATION)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                hCallback(H_ERROR_BIOMETRIC_VALIDATION)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hCreateBioMetricPrompt(
        hContext: Context,
        hPromptCallback: (BiometricPrompt.AuthenticationResult) -> Unit,
    ): BiometricPrompt {

        val hExecutor = ContextCompat.getMainExecutor(hContext)
        val hCallBack = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Timber.d("errCode is $errorCode and errString is: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.d("Authentication Succeeded")
                hPromptCallback(result)

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.d("Authentication failed")
            }
        }


        return BiometricPrompt(hContext as AppCompatActivity, hExecutor, hCallBack)
    }

    fun hCreatePromptInfo(bioMetricActivity: BioMetricActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(
                String.format(
                    bioMetricActivity.getString(R.string.prompt_info_title),
                    bioMetricActivity.getString(R.string.app_name)
                )
            )
            setSubtitle(bioMetricActivity.getString(R.string.prompt_info_subtitle))
            setDescription("Description")
            setConfirmationRequired(false)
            setNegativeButtonText(bioMetricActivity.getString(R.string.prompt_info_use_app_password))
        }.build()
}