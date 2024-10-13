package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.*
import com.example.gceolmcqs.repository.RemoteRepoManager
import com.google.gson.Gson

class MainActivityViewModel : ViewModel() {
    private val _liveSubjectsAvailable = MutableLiveData<ArrayList<String>>()
    val liveSubjectsAvailable: LiveData<ArrayList<String>> = _liveSubjectsAvailable
    private lateinit var subjectAndFileNameDataListModel: SubjectAndFileNameDataListModel
    private val subjectPackageDataList = ArrayList<SubjectPackageData>()


    fun updateSubjectPackageDataList() {
        val temp = RemoteRepoManager.getUserSubjectPackages().subjectPackageDataList
        subjectPackageDataList.clear()
        subjectPackageDataList.addAll(temp)
    }

    fun getSubjectPackageDataList(): ArrayList<SubjectPackageData>{
        return subjectPackageDataList
    }

    fun getSubjectAndFileNameDataAt(position: Int): SubjectAndFileNameData {
        return subjectAndFileNameDataListModel.subjectAndFileNameDataList[position]
    }

    fun setSubjectAndFileNameDataListModel(subjectsDataJsonString: String?) {
        subjectAndFileNameDataListModel =
            Gson().fromJson(subjectsDataJsonString!!, SubjectAndFileNameDataListModel::class.java)
        val subjectAndFile = subjectAndFileNameDataListModel.subjectAndFileNameDataList
        setSubjectNames(subjectAndFile)

    }

    private fun setSubjectNames(temp: ArrayList<SubjectAndFileNameData>) {
        val tempSubjectNames = ArrayList<String>()
        temp.forEach {
            tempSubjectNames.add(it.subject)
        }

        _liveSubjectsAvailable.value = tempSubjectNames
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
}

