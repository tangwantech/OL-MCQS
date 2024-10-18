package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.datamodels.ExamItemData
import com.example.gceolmcqs.datamodels.ExamTypeData
import com.example.gceolmcqs.datamodels.SubjectData
import com.example.gceolmcqs.datamodels.SubjectPackageData
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.RemoteRepoManager
//import com.example.gceolmcqs.roomDB.GceOLMcqDatabase
import com.google.gson.Gson

class SubjectContentTableViewModel : ViewModel() {
    private lateinit var subjectName: String
    private val isSubjectPackageActive = MutableLiveData<Boolean>()
    private var subjectIndex: Int? = null

    private val _subjectPackageData = MutableLiveData<SubjectPackageData>()
    val subjectPackageData: LiveData<SubjectPackageData> = _subjectPackageData

    fun getSubjectPackageDataFromRemoteRepoAtIndex(index: Int){
        _subjectPackageData.value = RemoteRepoManager.getSubjectPackageDataAtIndex(index)
    }

    fun getExamTitles(): List<String?> {
        return AppDataRepository.getExamTitles(subjectIndex!!)
    }

    fun getExamTypesCount(): Int {
        return AppDataRepository.getExamTitles(subjectIndex!!).size
    }

    fun getIsPackageActive(): LiveData<Boolean> {
        return isSubjectPackageActive
    }

    fun getPackageStatus(): Boolean{
        return ActivationExpiryDatesGenerator().checkExpiry(_subjectPackageData.value!!.activatedOn!!, _subjectPackageData.value!!.expiresOn!!)
    }

    fun setSubjectIndex(index: Int) {
        subjectIndex = index
    }

    fun getSubjectName(): String{
        return AppDataRepository.getSubjectName(subjectIndex!!)
    }
}