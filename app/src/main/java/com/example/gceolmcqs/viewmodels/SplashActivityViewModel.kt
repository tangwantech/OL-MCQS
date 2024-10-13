package com.example.gceolmcqs.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.SubjectAndFileNameData
import com.example.gceolmcqs.datamodels.SubjectAndFileNameDataListModel
import com.example.gceolmcqs.repository.RemoteRepoManager

import com.google.gson.Gson

class SplashActivityViewModel : ViewModel() {

    private val iSubjectPackageAvailable = MutableLiveData<String>()
    private val _liveSubjectNames = MutableLiveData<ArrayList<String>>()
    val liveSubjectNames: LiveData<ArrayList<String>> = _liveSubjectNames


    private lateinit var subjectAndFileNameDataListModel: SubjectAndFileNameDataListModel


    fun verifyDeviceIdInAppDatabase(callback: RemoteRepoManager.OnVerifyDataExistsListener){
        RemoteRepoManager.verifyDeviceIdInAppDatabase(callback)
    }

    fun getSubjectAndFileNameDataAt(position: Int): SubjectAndFileNameData {
        return subjectAndFileNameDataListModel.subjectAndFileNameDataList[position]
    }


    fun setSubjectAndFileNameDataListModel(subjectsDataJsonString: String?) {
        subjectAndFileNameDataListModel =
            Gson().fromJson(subjectsDataJsonString!!, SubjectAndFileNameDataListModel::class.java)

        setSubjectNames()

    }

    private fun setSubjectNames() {
        val tempSubjectNames = ArrayList<String>()
        val subjectAndFileList = subjectAndFileNameDataListModel.subjectAndFileNameDataList
        subjectAndFileList.forEach {
            tempSubjectNames.add(it.subject)
        }

        _liveSubjectNames.value = tempSubjectNames
    }



}