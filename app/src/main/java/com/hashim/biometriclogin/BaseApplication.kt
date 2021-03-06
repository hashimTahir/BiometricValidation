/*
 * Copyright (c) 2021/  4/ 14.  Created by Hashim Tahir
 */

package com.hashim.biometriclogin

import android.app.Application
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        hInitTimber()
    }


    private fun hInitTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(
                    priority: Int,
                    tag: String?,
                    message: String,
                    t: Throwable?
                ) {
                    super.log(
                        priority,
                        String.format(Constants.hTag, tag),
                        message,
                        t
                    )
                }
            })
        }
    }
}