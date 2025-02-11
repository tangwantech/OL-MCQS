package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.datamodels.ActivationExpiryDates

import com.example.gceolmcqs.datamodels.SubjectPackageData
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.RemoteRepoManager


class SubjectContentTableViewModel : ViewModel() {
    private lateinit var subjectName: String
    private val isSubjectPackageActive = MutableLiveData<Boolean>()
    private var subjectIndex: Int? = null

    private val _subjectPackageData = MutableLiveData<SubjectPackageData>()
    val subjectPackageData: LiveData<SubjectPackageData> = _subjectPackageData

    fun loadSubjectPackageDataFromRemoteRepoAtIndex(index: Int){
        _subjectPackageData.value = RemoteRepoManager.getSubjectPackageDataAtIndex(index)
    }

    fun getExamTitles(): List<String?> {
        return AppDataRepository.getExamTitles(subjectIndex!!)
    }

    fun getExamTypesCount(): Int {
        return AppDataRepository.getExamTitles(subjectIndex!!).size
    }

    fun getIsPackageActive(): LiveData<Boolean> {a
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

    fun getGraceExtension(): ActivationExpiryDates {
//        println("packageName: ${subjectPackageData.value!!.packageName!!}")
        return ActivationExpiryDatesGenerator.extendExpiryDate(subjectPackageData.value!!.expiresOn!!, subjectPackageData.value!!.packageName!!)
    }
}