package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.AppDataUpdater
import com.example.gceolmcqs.UsageTimer
import com.example.gceolmcqs.datamodels.*
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.RemoteRepoManager

class MainActivityViewModel : ViewModel() {
    private val subjectPackageDataList = ArrayList<SubjectPackageData>()

    fun updateSubjectPackageDataList() {
        val temp = RemoteRepoManager.getUserSubjectPackages().subjectPackageDataList
        subjectPackageDataList.clear()
        subjectPackageDataList.addAll(temp)
    }

    fun getSubjectPackageDataList(): ArrayList<SubjectPackageData>{
        return subjectPackageDataList
    }

    fun updatePackageStatusAt(index: Int, updateCallBack: RemoteRepoManager.OnUpdatePackageListener){
        subjectPackageDataList[index].isPackageActive = false
        updateSubjectPackageDataInRemoteDb(subjectPackageDataList[index], updateCallBack)
    }

    private fun updateSubjectPackageDataInRemoteDb(subjectPackageData: SubjectPackageData,  updateCallBack: RemoteRepoManager.OnUpdatePackageListener){
        RemoteRepoManager.updateUserSubjectPackages(subjectPackageData, object : RemoteRepoManager.OnUpdatePackageListener{
            override fun onUpDateSuccessful(index: Int) {
                updateCallBack.onUpDateSuccessful(index)
            }

            override fun onError() {
                updateCallBack.onError()
            }

        })
    }

    fun initAppData(){
        AppDataRepository.initAppData()
//        println(AppDataRepository.getSubjectNames())
//        println(AppDataRepository.getExamTitles(0))
    }

    fun updateAppData(appDataUpdateListener: AppDataUpdater.AppDataUpdateListener) {
        AppDataUpdater.update(appDataUpdateListener)
    }


}

