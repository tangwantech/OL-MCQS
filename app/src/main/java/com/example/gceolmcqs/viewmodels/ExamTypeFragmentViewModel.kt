package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.ExamItemData
import com.example.gceolmcqs.datamodels.ExamTypeData
import com.example.gceolmcqs.repository.AppDataRepository

class ExamTypeFragmentViewModel: ViewModel() {
    private lateinit var examTypeData: ExamTypeData

//    fun getExamItemDataAt(position: Int):ExamItemData{
//        return examTypeData.examItems[position]
//    }

    fun getExamItemTitles(subjectIndex: Int, fragmentIndex: Int): ArrayList<String>{
        val examItemTitles = ArrayList<String>()
        examItemTitles.addAll(AppDataRepository.getExamItemTitles(subjectIndex, fragmentIndex))
        return examItemTitles
    }
}