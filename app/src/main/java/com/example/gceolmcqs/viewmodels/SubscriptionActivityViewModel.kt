package com.example.gceolmcqs.viewmodels

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.MomoPayService

import com.example.gceolmcqs.SubjectPackageActivator
import com.example.gceolmcqs.datamodels.PackageData
import com.example.gceolmcqs.datamodels.SubjectPackageData
import com.example.gceolmcqs.datamodels.SubscriptionFormData
import com.example.gceolmcqs.repository.RemoteRepoManager

class SubscriptionActivityViewModel: ViewModel() {
    private lateinit var momoPay: MomoPayService

    private var _subscriptionData = SubscriptionFormData()

    private val _transactionStatus = MutableLiveData<String?>()
    val transactionStatus: LiveData<String?> = _transactionStatus

    private var _refNumber: String? = null

    private val _packageUpdateStatus = MutableLiveData<Boolean>()
    val packageUpdateStatus: LiveData<Boolean> = _packageUpdateStatus

    fun initSubscriptionFormData(subjectIndex: Int, subjectName: String){
        _subscriptionData.subjectPosition = subjectIndex
        _subscriptionData.subject = subjectName
    }

    fun updateSubscriptionPackageTypePriceAndDuration(packageData: PackageData){
        _subscriptionData.packageType = packageData.packageName
        _subscriptionData.packagePrice = packageData.price
        _subscriptionData.packageDuration = packageData.duration
    }

    fun updateMoMoPartner(momoPartner: String){
        _subscriptionData.momoPartner = momoPartner
    }

    fun updateMomoNumber(momoNumber: String){
        _subscriptionData.momoNumber = momoNumber
    }


    fun setMomoPayService(momoPayService: MomoPayService){
        momoPay = momoPayService

    }

    fun activateSubjectPackage() {
        val subjectIndex = _subscriptionData.subjectPosition!!
        val subjectName = _subscriptionData.subject!!
        val packageType = _subscriptionData.packageType!!
        val packageDuration = _subscriptionData.packageDuration!!
        val activatedSubjectPackageData = SubjectPackageActivator.activateSubjectPackage(subjectName, subjectIndex, packageType, packageDuration)
        updateActivatedPackageInRemoteRepo(activatedSubjectPackageData)
    }

    fun getSubjectPackageType(): String{
        return _subscriptionData.packageType!!
    }

    fun getSubjectName(): String{
        return _subscriptionData.subject!!
    }

    fun getMomoNumber(): String{
        return _subscriptionData.momoNumber!!
    }

    fun getPackagePrice(): String{
        return _subscriptionData.packagePrice!!
    }

    fun getMomoPartner(): String{
        return _subscriptionData.momoPartner!!
    }

    fun initiatePayment(){

        momoPay.initiatePayment(_subscriptionData, object: MomoPayService.TransactionStatusListener{
            override fun onTransactionTokenAvailable(token: String?) {
                println(token)
//                updateCurrentTransactionToken(token!!)
            }

            override fun onTransactionIdAvailable(transactionId: String?) {
//                println("Transaction id: $transactionId")

            }

            override fun onReferenceNumberAvailable(refNum: String?) {
                _refNumber = refNum
            }

            override fun onTransactionPending() {
//                println("Transaction pending......")
                _transactionStatus.postValue(MCQConstants.PENDING)


            }

            override fun onTransactionFailed() {
//                println("Transaction failed.......")
                _transactionStatus.postValue(MCQConstants.FAILED)
//                updateCurrentTransactionStatus(MCQConstants.FAILED)

            }

            override fun onTransactionSuccessful() {
//                println("Transaction successful.....")
                _transactionStatus.postValue(MCQConstants.SUCCESSFUL)
//                updateCurrentTransactionStatus(MCQConstants.SUCCESSFUL)

            }

        })

    }

//    fun getIsPaymentSystemAvailable():LiveData<Boolean?>{
//        return momoPay.isPaymentSystemAvailable
//    }

    private fun updateActivatedPackageInRemoteRepo(activatedSubjectPackageData: SubjectPackageData){
        RemoteRepoManager.updateUserSubjectPackages(activatedSubjectPackageData, object: RemoteRepoManager.OnUpdatePackageListener{
            override fun onUpDateSuccessful(index: Int) {
               _packageUpdateStatus.value = true
            }

            override fun onError() {
                _packageUpdateStatus.value = false
            }

        })
    }



}
