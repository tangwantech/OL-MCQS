package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.UsageTimer
import com.example.gceolmcqs.datamodels.*
import com.example.gceolmcqs.repository.AppDataRepository
import com.example.gceolmcqs.repository.PaperRepository
import com.example.gceolmcqs.repository.RemoteRepoManager

class PaperActivityViewModel:ViewModel() {
    private var currentFragmentIndex: Int? = null
    private lateinit var subjectName: String
    private var subjectIndex: Int = 0

    fun getExamItemTitle(subjectIndex: Int, examTypeIndex: Int, examItemIndex: Int): String{
        return AppDataRepository.getExamItemTitle(subjectIndex, examTypeIndex, examItemIndex)
    }

    fun setCurrentFragmentIndex(index: Int){
        currentFragmentIndex = index
    }

    fun getCurrentFragmentIndex():Int?{
        return currentFragmentIndex
    }

    fun setSubjectName(subjectName: String) {
        this.subjectName = subjectName
    }

    fun initPaperData(subjectIndex: Int, examTypeIndex: Int, examItemIndex: Int){
        PaperRepository.initPaperData(subjectIndex, examTypeIndex, examItemIndex)

    }

    fun getUnAnsweredSectionIndexes(): List<Int>{
        return PaperRepository.getUnAnsweredSectionIndexes()
    }

    fun setCurrentSectionIndex(sectionIndex: Int){
        PaperRepository.setCurrentSectionIndex(sectionIndex)
    }

    fun getCurrentSectionIndex(): Int {
        return PaperRepository.getCurrentSectionIndex()
    }

    fun getTotalNumberOfQuestions():Int{
        return PaperRepository.getTotalNumberOfQuestions()
    }

    fun getSectionData(position: Int):SectionData{
        return PaperRepository.getSectionDataAt(position)
    }

    fun getNumberOfSections(): Int{
        return PaperRepository.getNumberOfSections()
    }

    fun updateSectionsScore(sectionIndex: Int, score: Int){
        PaperRepository.updateSectionScoreAt(sectionIndex, score)

    }

    fun resetSectionScore(sectionIndex: Int){
        PaperRepository.resetSectionScoreAt(sectionIndex)
    }

    fun updateIsSectionsAnswered(sectionIndex: Int){
        PaperRepository.updateSectionsAnsweredAt(sectionIndex)
    }

    fun getIsSectionsAnswered(): List<Boolean>{
        return PaperRepository.getSectionsAnswered()
    }

    fun decrementCurrentSectionRetryCount(){
        PaperRepository.decrementCurrentSectionRetryCount()

    }

    fun resetCurrentSectionRetryCount(){
        PaperRepository.resetCurrentSectionRetryCount()
    }

    fun getCurrentSectionRetryCount(): LiveData<Int>{
        return PaperRepository.getCurrentSectionRetryCount()
    }

    fun setUserMarkedAnswerSheet(userMarkedAnswersSheetData: UserMarkedAnswersSheetData){
        PaperRepository.setUserMarkedAnswerSheet(userMarkedAnswersSheetData)
    }

    fun getUserMarkedAnswerSheet(): UserMarkedAnswersSheetData {
        return PaperRepository.getUserMarkedAnswerSheet()
    }

    fun setSectionResultData(sectionResultData: SectionResultData){
        PaperRepository.setSectionResultData(sectionResultData)
    }

    fun getSectionResultData(): SectionResultData {
        return PaperRepository.getSectionResultData()
    }

    fun resetPaperRepository(){
        PaperRepository.resetPaperRepo()
    }

    fun isPackageActive(subjectIndex: Int): Boolean{

        val activatedOn = RemoteRepoManager.getSubjectPackageDataAtIndex(subjectIndex).activatedOn
        val expiresOn = RemoteRepoManager.getSubjectPackageDataAtIndex(subjectIndex).expiresOn
        return ActivationExpiryDatesGenerator().checkExpiry(activatedOn!!, expiresOn!!)
    }

    fun startUsageTime(subjectIndex: Int) {
        val activatedOn = RemoteRepoManager.getSubjectPackageDataAtIndex(subjectIndex).activatedOn
        val expiresOn = RemoteRepoManager.getSubjectPackageDataAtIndex(subjectIndex).expiresOn
        val timeRemaining = ActivationExpiryDatesGenerator.getTimeRemaining(activatedOn!!, expiresOn!!)
        UsageTimer.startUsageTimer(timeRemaining)
    }

    fun stopUsageTimer(){
        UsageTimer.stopTimer()
    }

    fun resetUsageTimerData() {
        UsageTimer.resetUsageTimerData()
    }

}