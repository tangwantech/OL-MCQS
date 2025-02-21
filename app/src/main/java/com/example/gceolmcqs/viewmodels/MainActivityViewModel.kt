package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.AppDataUpdater
import com.example.gceolmcqs.UsageTimer
import com.example.gceolmcqs.datamodels.*
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.RemoteRepoManager
import com.parse.ParseQuery
import com.parse.ParseUser

class MainActivityViewModel : ViewModel() {
    private val subjectPackageDataList = ArrayList<SubjectPackageData>()
    private var indexOfCurrentSubject: Int? = null
    private val _usageTimeBonus = MutableLiveData<Long>()
    val usageTimeBonus: LiveData<Long> = _usageTimeBonus




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

//        CoroutineScope(Dispatchers.IO).launch {
//            queryTest()
//        }

    }

    fun updateAppData(appDataUpdateListener: AppDataUpdater.AppDataUpdateListener) {
        AppDataUpdater.update(appDataUpdateListener)
    }

//    private fun queryTest(){
//        val query = ParseQuery.getQuery<ParseUser>("_User")
//        query.findInBackground{users, e ->
//            if (e == null){
//                for (user in users){
//                    println("Back4App: User-> ${user.username}, subjectPackages: ${user.getString("subjectPackages")}")
//                }
//            }else{
//                println("Back4app error: ${e.message}")
//            }
//        }
//    }

    fun setIndexOfCurrentSubject(index: Int){
        indexOfCurrentSubject = index
    }

    fun getIndexOfCurrentSubject(): Int?{
        return indexOfCurrentSubject
    }

    fun calculateNewBonusTime(oldBonus: Long, bonusTimeDiscount: Double){
        _usageTimeBonus.value = UsageTimer.getNewBonusTime(oldBonus, bonusTimeDiscount)
    }

    fun resetUsageTimer(){
        UsageTimer.resetUsageTimerData()
    }

    fun extentSubjectPackageAt(subjectIndex: Int, bonusTime: Long, updateCallBack: RemoteRepoManager.OnUpdatePackageListener){

        val subjectPackageData = getSubjectPackageDataList()[subjectIndex]
//        println(subjectPackageData)
        val newExpiryDate = ActivationExpiryDatesGenerator.getNewExpiryDate(subjectPackageData.expiresOn!!, bonusTime)
        subjectPackageData.expiresOn = newExpiryDate
        updateSubjectPackageDataInRemoteDb(subjectPackageData, updateCallBack)
    }


}

