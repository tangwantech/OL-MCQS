package com.example.gceolmcqs.viewmodels


import androidx.lifecycle.ViewModel

import com.example.gceolmcqs.repository.RemoteRepoManager
import com.example.gceolmcqs.repository.RemoteRepoManager.OnAppDataAvailableListener


class SplashActivityViewModel : ViewModel() {

    fun verifyDeviceIdInAppDatabase(callback: RemoteRepoManager.OnVerifyDataExistsListener){

        RemoteRepoManager.verifyDeviceIdInAppDatabase(callback)
    }


    fun getAppData(appDataAvailableListener: OnAppDataAvailableListener){
        RemoteRepoManager.getAppDataFromParse(appDataAvailableListener)
    }

    fun verifyAppDataAvailability(): Boolean{
        return RemoteRepoManager.verifyAppDataAvailability()
    }



}