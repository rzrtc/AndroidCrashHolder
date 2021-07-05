package com.crashapplication.sample

import android.app.Application
import com.rz.crashreport.XCrash
import com.rz.crashreport.XCrash.InitParameters

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize xCrash.
        XCrash.init(
            this, InitParameters()
                .setUploadUrl("https://data-center-dev.duobeiyun.com/tosee/v1/crash")
                .setJavaRethrow(true)
                .setJavaLogCountMax(10)
                .setLogDir("$filesDir/crashreport")
                .setJavaDumpAllThreadsWhiteList(arrayOf("^main$", "^Binder:.*", ".*Finalizer.*"))
                .setJavaDumpAllThreadsCountMax(10)
                .setNativeRethrow(true)
                .setNativeLogCountMax(10)
                .setNativeDumpAllThreadsWhiteList(
                    arrayOf(
                        "^Signal Catcher$",
                        "^Jit thread pool$",
                        ".*(R|r)ender.*",
                        ".*Chrome.*"
                    )
                )
                .setNativeDumpAllThreadsCountMax(10)
                .setAnrRethrow(true)
                .setAnrLogCountMax(10)
                .setPlaceholderCountMax(3)
                .setPlaceholderSizeKb(512)
                .setLogFileMaintainDelayMs(1000)
        )
    }
}