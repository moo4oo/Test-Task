package com.example.dima.testtask

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.vk.sdk.VKSdk

public class MyApp: Application(){

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(this)
        FacebookSdk.sdkInitialize(this)

        AppEventsLogger.activateApp(this)
    }

}