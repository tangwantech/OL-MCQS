package com.example.gceolmcqs.datamodels

data class AppData(val subjects: List<Subject>)
data class Subject(val title: String, val examTypes: List<ExamType>)
data class ExamType(val title: String, val examItems: List<ExamItem>)
data class ExamItem(val title: String, val paperData: PaperData)
