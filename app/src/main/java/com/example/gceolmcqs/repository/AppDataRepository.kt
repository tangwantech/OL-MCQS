package com.example.gceolmcqs.repository

import com.example.gceolmcqs.datamodels.AppData
import com.example.gceolmcqs.datamodels.PaperData

class AppDataRepository {
    companion object {
        private var appData: AppData? = null
        fun initAppData(){
            appData = RemoteRepoManager.getOLMCQData()
//            callBack.onAppDataInitialised()
        }
        fun getSubjectNames(): List<String>{
            val subjectNames = ArrayList<String>()
            appData?.subjects?.forEach {
                subjectNames.add(it.title)
            }
            return subjectNames
        }

        fun getSubjectName(subjectIndex: Int): String{
            return appData?.subjects!![subjectIndex].title
        }

        fun getExamTitles(subjectIndex: Int): List<String>{
            val contentTitles = ArrayList<String>()
            appData?.subjects!![subjectIndex].examTypes.forEach { examType ->
                contentTitles.add(examType.title)
            }
            return contentTitles
        }

        fun getExamItemTitles(subjectIndex: Int, examTypeIndex: Int): List<String>{
            val examItemTitles = ArrayList<String>()
            appData?.subjects!![subjectIndex].examTypes[examTypeIndex].examItems.forEach { examItem ->
                examItemTitles.add(examItem.title)
            }
            return examItemTitles
        }

        fun getExamItemTitle(subjectIndex: Int, examTypeIndex: Int, examItemIndex: Int): String{
            return appData?.subjects!![subjectIndex].examTypes[examTypeIndex].examItems[examItemIndex].title
        }

        fun getPaperData(subjectIndex: Int, contentIndex: Int, examItemIndex: Int): PaperData{
            return appData?.subjects!![subjectIndex].examTypes[contentIndex].examItems[examItemIndex].paperData
        }

    }

    interface OnAppDataInitialiseListener{
        fun onAppDataInitialised()
    }
}