package com.example.gceolmcqs.repository

import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.datamodels.AppData
import com.example.gceolmcqs.datamodels.PaperData
import com.parse.ParseUser

class AppDataRepository {
    companion object {
        private var appData: AppData? = null
        fun initAppData(callBack: OnAppDataInitialiseListener){
            appData = RemoteRepoManager.getOLMCQData()
            callBack.onAppDataInitialised()
        }
        fun getSubjectNames(): List<String>{
            val subjectNames = ArrayList<String>()
            appData?.subjects?.forEach {
                subjectNames.add(it.title)
            }
            return subjectNames
        }

        fun getExamTitles(subjectIndex: Int): List<String>{
            val contentTitles = ArrayList<String>()
            appData?.subjects!![subjectIndex].examTypes.forEach { content ->
                contentTitles.add(content.title)
            }
            return contentTitles
        }

        fun getExamItemTitles(subjectIndex: Int, contentIndex: Int): List<String>{
            val examItemTitles = ArrayList<String>()
            appData?.subjects!![subjectIndex].examTypes[contentIndex].examItems.forEach { examItem ->
                examItemTitles.add(examItem.title)
            }
            return examItemTitles
        }

        fun getPaperData(subjectIndex: Int, contentIndex: Int, examItemIndex: Int): PaperData{
            return appData?.subjects!![subjectIndex].examTypes[contentIndex].examItems[examItemIndex].paperData
        }

    }

    interface OnAppDataInitialiseListener{
        fun onAppDataInitialised()
    }
}