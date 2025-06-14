package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.MomoPayService

import com.example.gceolmcqs.SubjectPackageActivator
import com.example.gceolmcqs.datamodels.PackageFormData
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

    private val _momoPartner = MutableLiveData<String>()
    val momoPartner: LiveData<String> = _momoPartner

    private var packageTypes: String? = null

    fun initSubscriptionFormData(subjectIndex: Int, subjectName: String){
        _subscriptionData.subjectPosition = subjectIndex
        _subscriptionData.subject = subjectName
    }

    fun updateSubscriptionPackageTypePriceAndDuration(packageFormData: PackageFormData){
        _subscriptionData.packageType = packageFormData.packageName
        _subscriptionData.packagePrice = packageFormData.price
        _subscriptionData.packageDuration = packageFormData.duration
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

            override fun onTransactionAvailable(transactionId: String?, ussdCode: String, operator: String) {
//                println("Transaction id: $transactionId")
                _momoPartner.postValue(operator)

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

            override fun onNetWorkError() {
                _transactionStatus.postValue(MCQConstants.NETWORK_ERROR)
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

    fun queryPackageTypesFromRemoteRepo(listener: RemoteRepoManager.OnQueryListener){
        RemoteRepoManager.queryPackageTypesFromRemoteServer(object: RemoteRepoManager.OnQueryPackagesTypeListener{
            override fun onResult(result: String) {
                packageTypes = result
                listener.onSuccess()
            }

            override fun onError(error: String) {
               listener.onError()
            }

        })
    }

    fun getPackageTypes(): String{
        return packageTypes!!
    }



}
