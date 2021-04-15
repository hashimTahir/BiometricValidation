/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.hashim.biometriclogin.crypto.BioMetricUtis
import com.hashim.biometriclogin.crypto.CryptoManagerImpl
import com.hashim.biometriclogin.databinding.ActivityBioMetricBinding

class BioMetricActivity : AppCompatActivity() {
    lateinit var hActivityBioMetricBinding: ActivityBioMetricBinding
    private val hBioMetricUtis = BioMetricUtis
    private val hCryptoManagerImpl = CryptoManagerImpl

    private val hMainViewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityBioMetricBinding = ActivityBioMetricBinding.inflate(layoutInflater)
        setContentView(hActivityBioMetricBinding.root)

        hSetupListeners()

        hSubscribeObservers()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hSubscribeObservers() {

        hMainViewModel.hLoginResultLd.observe(this) {
            it?.let {
                if (it.hSuccess) {
                    hMainViewModel.hCreateCompletlyNewPrompter()
                }
            }
        }
        hMainViewModel.hBioMetricResultLd.observe(this) {
            it?.let { bioMetricResult ->
                bioMetricResult.hCreatePompter.let {
                    if (it) {
                        val hPrompt = hBioMetricUtis.hCreateBioMetricPrompt(this) {
                            hMainViewModel.hEncryptAndStoreToken(it)
                            finish()
                        }
                        bioMetricResult.hCipher?.let {
                            val hPromptInfo = hBioMetricUtis.hCreatePromptInfo(this)
                            hPrompt.authenticate(hPromptInfo, BiometricPrompt.CryptoObject(it))

                        }

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hSetupListeners() {
        hActivityBioMetricBinding.hCancelB.setOnClickListener {
            finish()
        }

        hActivityBioMetricBinding.hAuthorizeB.setOnClickListener {
            hMainViewModel.hDoConventionalLogin(
                userName = hActivityBioMetricBinding.hUserNameTv.text.toString(),
                password = hActivityBioMetricBinding.hPasswordTv.text.toString(),
            )
        }


    }
}