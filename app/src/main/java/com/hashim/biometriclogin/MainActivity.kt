/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import com.hashim.biometriclogin.crypto.BioMetricUtis
import com.hashim.biometriclogin.databinding.ActivityMainBinding
import com.hashim.biometriclogin.events.LoginState
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var hActivityMainBinding: ActivityMainBinding
    private val hMainViewModel: MainViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)


        hMainViewModel.hCheckIfBiometricAuthenticationIsAvailable()

        hSetupListeners()

        hSubscribeObservers()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hSubscribeObservers() {

        hMainViewModel.hLoginResultLd.observe(this) { loginResult ->
            if (loginResult.hSuccess) {
                Timber.d("App loged in ")
            }

        }
        hMainViewModel.hLoginStateLd.observe(this) { loginState ->
            when (loginState) {
                is LoginState.SuccessLoginState -> {
                    hActivityMainBinding.hLoginButton.isEnabled = loginState.hIsDataValid
                }
                is LoginState.FailedLoginState -> {
                    hActivityMainBinding.hLoginButton.isEnabled = false
                    loginState.hUsernameError?.let {
                        hActivityMainBinding.hUserNameEt.error = it
                    }
                    loginState.hPasswordError?.let {
                        hActivityMainBinding.hPasswordEt.error = it
                    }
                }
            }

        }
        hMainViewModel.hBioMetricResultLd.observe(this) { bioMetricResult ->
            bioMetricResult?.let {
                it.hHasBioMetricValidation.let {
                    /*Means app has biometrics already enabled*/
                    if (it) {
                        hActivityMainBinding.hBiometricLogin.visibility = View.VISIBLE
                        hActivityMainBinding.hBiometricLogin.setOnClickListener {
                            hMainViewModel.hLoginWithBioMetricAuthentication()
                        }
                    } else {
                        hActivityMainBinding.hBiometricLogin.visibility = View.GONE

                    }


                }
                it.hOpenBioMetricEnablerIntnet.let {
                    if (it) {
                        startActivity(Intent(this, BioMetricActivity::class.java))
                    }
                }
                it.hMessage?.let { message ->
                    Toast.makeText(this, message, Toast.LENGTH_LONG)
                        .show()
                }
                it.hCreatePompter.let {

                    val hPrompt = BioMetricUtis.hCreateBioMetricPrompt(this) {
                        hMainViewModel.hDecryptToken(it)
                    }
                    bioMetricResult.hCipher?.let {
                        val hPromptInfo = BioMetricUtis.hCreatePromptInfo(this)
                        hPrompt.authenticate(
                            hPromptInfo,
                            BiometricPrompt.CryptoObject(bioMetricResult.hCipher)
                        )
                    }
                }
            }
        }
    }

    private fun hSetupListeners() {
        hActivityMainBinding.hLoginButton.setOnClickListener {
            hMainViewModel.hDoConventionalLogin(
                hActivityMainBinding.hUserNameEt.text.toString(),
                hActivityMainBinding.hPasswordEt.text.toString(),
            )
        }

        hActivityMainBinding.hUserNameEt.doAfterTextChanged {
            hMainViewModel.hDoConventionalLogin(
                hActivityMainBinding.hUserNameEt.text.toString(),
                hActivityMainBinding.hPasswordEt.text.toString(),
            )
        }
        hActivityMainBinding.hPasswordEt.doAfterTextChanged {
            hMainViewModel.hDoConventionalLogin(
                hActivityMainBinding.hUserNameEt.text.toString(),
                hActivityMainBinding.hPasswordEt.text.toString(),
            )
        }
    }


}

