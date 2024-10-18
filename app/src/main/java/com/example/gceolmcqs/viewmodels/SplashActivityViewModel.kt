package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.SubjectAndFileNameData
import com.example.gceolmcqs.datamodels.SubjectAndFileNameDataListModel
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.RemoteRepoManager
import com.example.gceolmcqs.repository.RemoteRepoManager.OnAppDataAvailableListener

import com.google.gson.Gson
import com.parse.SaveCallback

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