package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.ExamItemData
import com.example.gceolmcqs.datamodels.ExamTypeData
import com.example.gceolmcqs.repository.AppDataRepository

class ExamTypeFragmentViewModel: ViewModel() {
    private lateinit var examTypeData: ExamTypeData

    fun setExamTypeData(examTypeData: ExamTypeData){
        this.examTypeData = examTypeData
    }

    fun getExamTypeItemsData(): ArrayList<ExamItemData>{
        return examTypeData.examItems
    }
    fun getExamItemDataAt(position: Int):ExamItemData{
        return examTypeData.examItems[position]
    }

    fun getExamItemTitles(subjectIndex: Int, fragmentIndex: Int): ArrayList<String>{
        val examItemTitles = ArrayList<String>()
        AppDataRepository.getExamItemTitles(subjectIndex, fragmentIndex)
        return examItemTitles
    }
}