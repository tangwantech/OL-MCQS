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
    private lateinit var subjectData: SubjectData
    private val examTitles: ArrayList<String?> = ArrayList()
    private val examContents: HashMap<Int, ArrayList<String>?> = HashMap()
    private val examContentsFileNames: HashMap<String, String> = HashMap()
    private val isSubjectPackageActive = MutableLiveData<Boolean>()
    private var subjectIndex: Int? = null

//    private lateinit var gceOLMcqDatabase: GceOLMcqDatabase

    private val _subjectPackageData = MutableLiveData<SubjectPackageData>()
    val subjectPackageData: LiveData<SubjectPackageData> = _subjectPackageData

    fun initSubjectContentsData(jsonFile: String) {
        subjectData = Gson().fromJson(jsonFile, SubjectData::class.java)

        setExamTitles()
    }

    fun getSubjectPackageDataFromRemoteRepoAtIndex(index: Int){
        _subjectPackageData.value = RemoteRepoManager.getSubjectPackageDataAtIndex(index)
    }

    private fun setExamTitles() {
        subjectData.contents!!.forEachIndexed { index, examTypeData ->
            examTitles.add(examTypeData.title)
            setExamContents(examTypeData, index)
        }


    }

    fun getExamTypeDataAt(position: Int): ExamTypeData {
        return (subjectData.contents!![position])
    }

    fun getExamTitles(): List<String?> {

        return AppDataRepository.getExamTitles(subjectIndex!!)
    }

    fun getExamTypesCount(): Int {
        return AppDataRepository.getExamTitles(subjectIndex!!).size
//        return (subjectData.contents!!.size)
    }

    private fun setExamContents(examTypeData: ExamTypeData, index: Int) {
        val examContents: ArrayList<String> = ArrayList()

        examTypeData.examItems.forEach {
            examContents.add(it.title)
            setExamContentsFileNames(it)
        }
        this.examContents[index] = examContents

    }

    private fun setExamContentsFileNames(examItemData: ExamItemData) {
        examContentsFileNames[examItemData.title] = examItemData.fileName
    }

    fun getIsPackageActive(): LiveData<Boolean> {
        return isSubjectPackageActive
    }

    fun setSubjectName(subjectTitle: String) {
        subjectName = subjectTitle
    }
    fun getPackageStatus(): Boolean{
        return ActivationExpiryDatesGenerator().checkExpiry(_subjectPackageData.value!!.activatedOn!!, _subjectPackageData.value!!.expiresOn!!)
    }

    fun setSubjectIndex(index: Int) {
        subjectIndex = index
    }

    fun getSubjectIndex(): Int? {
        return subjectIndex
    }
}