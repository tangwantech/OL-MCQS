package com.example.gceolmcqs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment

import androidx.lifecycle.ViewModelProvider

import com.example.gceolmcqs.datamodels.PackageData

import com.example.gceolmcqs.fragments.PackagesDialogFragment
import com.example.gceolmcqs.fragments.PaymentMethodDialogFragment

import com.example.gceolmcqs.viewmodels.SubscriptionActivityViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SubscriptionActivity: AppCompatActivity(),
    PaymentMethodDialogFragment.OnPaymentMethodDialogListeners,
    PackagesDialogFragment.PackageDialogListener{

    private var processingAlertDialog: AlertDialog? = null
    private var requestToPayDialog: AlertDialog? = null
    private var failedToActivatePackageDialog: AlertDialog? = null
    private var activatingPackageDialog: AlertDialog? = null
    private var packageActivatedDialog: AlertDialog? = null
    private var paymentReceivedDialog: AlertDialog? = null
    private var packagesDialog: DialogFragment? = null
    private var paymentMethodDialog: DialogFragment? = null
    private var momoNumberInputDialog: AlertDialog? = null
    private var currentRefNum: String? = null

    private lateinit var viewModel: SubscriptionActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val subjectName = intent.getStringExtra(MCQConstants.SUBJECT_NAME)
        title = "$subjectName subscription"
        setupViewModel()
        setupViewObservers()
        showPackagesDialog()
    }


    private fun initSubscriptionFormData(){
        val subjectIndex = intent.getIntExtra(MCQConstants.SUBJECT_INDEX, 0)
        val subjectName = intent.getStringExtra(MCQConstants.SUBJECT_NAME)!!
        viewModel.initSubscriptionFormData(subjectIndex, subjectName)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[SubscriptionActivityViewModel::class.java]
        initSubscriptionFormData()
        viewModel.setMomoPayService(MomoPayService(this))

    }

    private fun setupViewObservers() {
        viewModel.packageUpdateStatus.observe(this){status ->

            status?.let{
                if (it){
                    if(activatingPackageDialog != null){
                        cancelActivatingPackageDialog()
                    }
                    if (packageActivatedDialog == null){
                        showPackageActivatedDialog()
                    }

                }

            }

        }


        setMomoPayFlow()

    }

    private fun setMomoPayFlow(){

        viewModel.transactionStatus.observe(this) {
//            it.status?.let
            it?.let{status ->
                when(status) {
                    MCQConstants.PENDING -> {
                        if (processingAlertDialog != null){
                            cancelProcessingRequestDialog()
                        }
                        if (failedToActivatePackageDialog != null){
                            cancelFailedToActivateDialog()
                        }

                        if (requestToPayDialog == null){
                            showRequestUserToPayDialog()
                        }

                    }
                    MCQConstants.SUCCESSFUL -> {
                        cancelProcessingAndRequestToPayDialogs()

                        if(paymentReceivedDialog == null){
                            showPaymentReceivedDialog()

                        }
                        if (failedToActivatePackageDialog != null){
                            cancelFailedToActivateDialog()
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            delay(2000)
                            withContext(Dispatchers.Main){
                                if(paymentReceivedDialog != null){
                                    cancelPaymentReceivedDialog()
                                }
                                if(activatingPackageDialog == null){
                                    showActivatingPackageDialog()
                                    activateUserPackage()
                                }
                            }
                        }
                    }
                    MCQConstants.FAILED -> {
                        cancelProcessingAndRequestToPayDialogs()

                        if(failedToActivatePackageDialog == null){
                            showTransactionFailedDialog()

                        }
                    }
                }
            }


        }


    }


    private fun showProcessingRequestDialog() {
        processingAlertDialog = AlertDialog.Builder(this).create()
        processingAlertDialog?.apply {
            setCancelable(false)
        }
        processingAlertDialog?.setMessage(resources.getString(R.string.processing_request))
        processingAlertDialog?.show()
    }

    private fun cancelProcessingRequestDialog() {
        processingAlertDialog?.dismiss()
        processingAlertDialog = null
    }

    private fun showActivatingPackageDialog() {
        activatingPackageDialog = AlertDialog.Builder(this).create()
        activatingPackageDialog?.apply {
            setCancelable(false)
        }
        activatingPackageDialog?.setMessage(resources.getString(R.string.activating_package_message))
        activatingPackageDialog?.show()
//        activateUserPackage()
    }

    private fun cancelActivatingPackageDialog() {
        activatingPackageDialog?.dismiss()
        activatingPackageDialog = null
    }



    private fun showRequestUserToPayDialog() {

        val dialogView = layoutInflater.inflate(R.layout.fragment_request_to_pay, null)
        val tvRequestToPayTitle: TextView = dialogView.findViewById(R.id.tvRequestToPayTitle)
        val tvRequestToPayMessage: TextView = dialogView.findViewById(R.id.tvRequestToPayMessage)
        val tvRequestToPaySubject: TextView =
            dialogView.findViewById(R.id.tvRequestToPaySubject)
        val tvRequestToPayPackageType: TextView =
            dialogView.findViewById(R.id.tvRequestToPayPackage)
        val tvRequestToPayPackagePrice: TextView =
            dialogView.findViewById(R.id.tvRequestToPayAmount)
        val tvTransactionId: TextView = dialogView.findViewById(R.id.tvTransactionId)

        if (viewModel.getMomoPartner() == MCQConstants.MTN_MOMO) {
            tvRequestToPayTitle.setBackgroundColor(resources.getColor(R.color.mtn))
            tvRequestToPayMessage.text = resources.getString(R.string.mtn_request_to_pay_message)

        } else {
            tvRequestToPayTitle.setBackgroundColor(resources.getColor(R.color.orange))
            tvRequestToPayMessage.text = resources.getString(R.string.orange_request_to_pay_message)
        }

        tvRequestToPaySubject.text = viewModel.getSubjectName()
        tvRequestToPayPackageType.text = viewModel.getSubjectPackageType()
        tvRequestToPayPackagePrice.text = "${viewModel.getPackagePrice()} FCFA"
//        tvTransactionId.text = "Reference Number: $currentRefNum"

        requestToPayDialog = AlertDialog.Builder(this).create()
        requestToPayDialog?.apply {
            setCancelable(false)
        }
        requestToPayDialog?.setView(dialogView)
        requestToPayDialog?.show()

    }

    private fun cancelRequestToPayDialog() {
//        requestToPayDialog.cancel()
        requestToPayDialog?.dismiss()
        requestToPayDialog =  null
    }

    private fun cancelProcessingAndRequestToPayDialogs(){
        if(processingAlertDialog != null){
            cancelProcessingRequestDialog()
        }

        if(requestToPayDialog != null){
            cancelRequestToPayDialog()
        }
    }

    private fun showPackageActivatedDialog() {
        val view = layoutInflater.inflate(
            R.layout.package_activation_successful_dialog,
            null
        )
        val tvPackageActivationSuccessful: TextView =
            view.findViewById(R.id.tvPackageActivationSuccessful)
        tvPackageActivationSuccessful.text =
            "${viewModel.getSubjectPackageType()} ${resources.getString(R.string.activated_successfully)}"
        val packageActivatedDialog = AlertDialog.Builder(this).apply {
            setView(view)
            setPositiveButton("Ok"){ _, _ ->
            exitActivity()
            }
            setCancelable(false)
        }.create()
        packageActivatedDialog.show()
    }

    private fun showTransactionFailedDialog() {

        failedToActivatePackageDialog = AlertDialog.Builder(this).create()
        val view = this.layoutInflater.inflate(R.layout.package_activation_failed_dialog, null)
        val tvFailedMessage: TextView = view.findViewById(R.id.tvPackageActivationFailed)
        tvFailedMessage.text =
            "${resources.getString(R.string.failed_to_activate_package)} ${viewModel.getSubjectPackageType()} "

        failedToActivatePackageDialog?.setView(view)
        failedToActivatePackageDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ ->
            cancelFailedToActivateDialog()
            exitActivity()

        }
        failedToActivatePackageDialog?.show()

    }

    private fun cancelFailedToActivateDialog(){
        failedToActivatePackageDialog?.dismiss()
        failedToActivatePackageDialog = null
    }

    private fun showPaymentReceivedDialog(){
        paymentReceivedDialog = AlertDialog.Builder(this).create()
        paymentReceivedDialog?.setMessage(resources.getString(R.string.payment_received))
        paymentReceivedDialog?.show()
    }

    private fun cancelPaymentReceivedDialog(){
        paymentReceivedDialog?.dismiss()
        paymentReceivedDialog = null
    }

    private fun displayInvoice(){
        val view = LayoutInflater.from(this).inflate(R.layout.subscription_summary_layout, null)
        val subjectName: TextView = view.findViewById(R.id.invoiceSubjectNameTv)
        val packageName: TextView = view.findViewById(R.id.invoicePackageNameTv)
        val packagePrice: TextView = view.findViewById(R.id.invoicePackagePriceTv)
        val momoNumber: TextView = view.findViewById(R.id.invoiceMomoNumberTv)

        subjectName.text = "${viewModel.getSubjectName()}"
        packageName.text = "${viewModel.getSubjectPackageType()}"
        packagePrice.text = "${viewModel.getPackagePrice()} FCFA"
        momoNumber.text = "${viewModel.getMomoNumber()}"
        val dialog = AlertDialog.Builder(this)
        dialog.apply{
            setMessage(resources.getString(R.string.verify_payment_info))
            setView(view)
            setCancelable(false)
            setPositiveButton(resources.getString(R.string.pay)  ){btn, _ ->
                showProcessingRequestDialog()
                viewModel.initiatePayment()
                btn.dismiss()
            }
            setNegativeButton(resources.getString(R.string.cancel)){btn, _ ->
                exitActivity()
            }
        }.create()
        dialog.show()
    }

    private fun showPackagesDialog(){
        packagesDialog = PackagesDialogFragment.newInstance()
        packagesDialog?.show(supportFragmentManager, null)
    }

    private fun showPaymentMethodDialog() {
        paymentMethodDialog = PaymentMethodDialogFragment.newInstance()
        paymentMethodDialog?.isCancelable = false
        paymentMethodDialog?.show(supportFragmentManager, null)
    }

    private fun displayEnterMomoNumberDialog(){
        val view = layoutInflater.inflate(R.layout.momo_number_dialog, null)
        val etMoMoNumber: TextInputEditText = view.findViewById(R.id.etMomoNumber)
        momoNumberInputDialog = AlertDialog.Builder(this).create()
        momoNumberInputDialog?.setTitle(resources.getString(R.string.enter_number))
        momoNumberInputDialog?.setView(view)
        momoNumberInputDialog?.setCancelable(false)
        momoNumberInputDialog?.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next)){ _, _ ->
            displayInvoice()
        }
        momoNumberInputDialog?.setButton(AlertDialog.BUTTON_NEGATIVE,resources.getString(R.string.cancel)){ _, _ ->
            exitActivity()
        }
        momoNumberInputDialog?.show()

        val btnPositive = momoNumberInputDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive?.isEnabled = false

        etMoMoNumber.doOnTextChanged { text, _, _, _ ->
            if(text.toString().isNotEmpty() && text.toString().length == 9){
//                subscriptionFormData.momoNumber = text.toString()
                viewModel.updateMomoNumber(text.toString())
                btnPositive?.isEnabled = true

            }else{
                btnPositive?.isEnabled = false
            }
        }
    }

    private fun activateUserPackage() {
        viewModel.activateSubjectPackage()
    }

    override fun onPackageDialogNextButtonClicked() {
        packagesDialog?.dismiss()
        showPaymentMethodDialog()
    }

    override fun onPackageDialogCancelButtonClicked() {
        exitActivity()
    }

    override fun onPackageItemSelected(packageData: PackageData) {
        viewModel.updateSubscriptionPackageTypePriceAndDuration(packageData)
    }

    private fun exitActivity(){
        finish()
    }

    companion object{
        fun getIntent(context: Context, subjectIndex: Int, subjectName: String): Intent {
            val intent = Intent(context, SubscriptionActivity::class.java)
            intent.putExtra(MCQConstants.SUBJECT_INDEX, subjectIndex)
            intent.putExtra(MCQConstants.SUBJECT_NAME, subjectName)
            return intent
        }
    }

    override fun onPaymentMethodNextButtonClicked() {
        paymentMethodDialog?.dismiss()
        displayEnterMomoNumberDialog()
    }

    override fun onPaymentTypeSelected(momoPartner: String) {
        viewModel.updateMoMoPartner(momoPartner)
    }

    override fun onPaymentMethodCancelButtonClicked(){
        exitActivity()
    }

}