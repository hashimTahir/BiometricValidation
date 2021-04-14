/*
 * Copyright (c) 2021/  4/ 15.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hashim.biometriclogin.data.TestUser
import com.hashim.biometriclogin.databinding.ActivityBioMetricBinding
import java.util.*

class BioMetricActivity : AppCompatActivity() {
    lateinit var hActivityBioMetricBinding: ActivityBioMetricBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityBioMetricBinding = ActivityBioMetricBinding.inflate(layoutInflater)
        setContentView(hActivityBioMetricBinding.root)

        hSetupListeners()
    }

    private fun hSetupListeners() {
        hActivityBioMetricBinding.hCancelB.setOnClickListener {
            finish()
        }

        hActivityBioMetricBinding.hAuthorizeB.setOnClickListener {
            hLoginUser(hActivityBioMetricBinding.hUserNameTv.text.toString())
        }


    }

    private fun hLoginUser(userName: String) {
        val hTextUser = hCreateFakeUser(userName)
        hShowBiometricPromptForEncryption()
    }

    private fun hShowBiometricPromptForEncryption() {
        TODO("Not yet implemented")
    }

    private fun hCreateFakeUser(userName: String) = TestUser(
        hToken = UUID.randomUUID().toString(),
        hUserName = userName
    )
}