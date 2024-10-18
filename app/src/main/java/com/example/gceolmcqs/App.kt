package com.example.gceolmcqs

import android.app.Application
import android.provider.Settings
import com.example.gceolmcqs.datamodels.CampayCredential
import com.example.gceolmcqs.repository.RemoteRepoManager
import com.google.gson.Gson
import com.parse.Parse
import net.compay.android.CamPay

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initParse()
        initRemoteRepoManager()
        initCampay()

    }

    private fun initParse(){
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        )
    }

    private fun initRemoteRepoManager(){
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        RemoteRepoManager.setDeviceID(deviceId)
    }


    private fun initCampay() {
        CamPay.init(
            getString(R.string.campay_app_user_name),
            getString(R.string.campay_app_pass_word),
            CamPay.Environment.DEV // environment
        )
    }


}