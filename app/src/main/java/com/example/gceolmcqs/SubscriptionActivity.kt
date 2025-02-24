package com.example.gceolmcqs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment

import androidx.lifecycle.ViewModelProvider

import com.example.gceolmcqs.datamodels.PackageFormData

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

//    private var processingAlertDialog: AlertDialog? = null
//    private var requestToPayDialog: AlertDialog? = null
//    private var failedToActivatePackageDialog: AlertDialog? = null
//    private var activatingPackageDialog: AlertDialog? = null
//    private var packageActivatedDialog: AlertDialog? = null
//    private var paymentReceivedDialog: AlertDialog? = null
//    private var momoNumberInputDialog: AlertDialog? = null

    private var dialog: AlertDialog? = null

//    private var packagesDialog: DialogFragment? = null
    private var dialogFragment: DialogFragment? = null

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
                    showPackageActivatedDialog()
                }

            }

        }

        viewModel.momoPartner.observe(this){
            when (it){
                MCQConstants.OPERATOR_MTN -> {
                    println("MTN oooooooh")
                    showRequestUserToPayDialog(MCQConstants.OPERATOR_MTN)
                }
                MCQConstants.OPERATOR_ORANGE -> {
                    showRequestUserToPayDialog(MCQConstants.OPERATOR_ORANGE)
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
//                        showRequestUserToPayDialog()

                    }
                    MCQConstants.SUCCESSFUL -> {
                        showPaymentReceivedDialog()

                        CoroutineScope(Dispatchers.IO).launch {
                            delay(2000)
                            withContext(Dispatchers.Main){
                                showActivatingPackageDialog()
                                activateUserPackage()
                            }
                        }
                    }
                    MCQConstants.FAILED -> {
                        showTransactionFailedDialog()
                    }
                    MCQConstants.NETWORK_ERROR -> {
                        showNetWorkErrorDialog()
                    }
                }
            }


        }


    }


    private fun showProcessingRequestDialog() {
        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.processing_request))
            setCancelable(false)
        }.create()
        dialog?.show()
    }

    private fun showActivatingPackageDialog() {
        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.activating_package_message))
            setCancelable(false)
        }.create()
        dialog?.show()
//        activateUserPackage()
    }

    private fun showRequestUserToPayDialog(momoPartner: String) {

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

//        viewModel.getMomoPartner() == MCQConstants.MTN_MOMO
        if (momoPartner == MCQConstants.OPERATOR_MTN) {
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

        if (dialog != null){
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(false)
        }.create()
        dialog?.show()

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

        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setView(view)
            setPositiveButton("Ok"){ _, _ ->
            exitActivity()
            }
            setCancelable(false)
        }.create()
        dialog?.show()
    }

    private fun showTransactionFailedDialog() {
        val view = layoutInflater.inflate(R.layout.package_activation_failed_dialog, null)
        val tvFailedMessage: TextView = view.findViewById(R.id.tvPackageActivationFailed)
        tvFailedMessage.text =
            "${resources.getString(R.string.failed_to_activate_package)} ${viewModel.getSubjectPackageType()} "

        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setView(view)
            setCancelable(false)
        }.create()

        dialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ ->
            exitActivity()

        }

        dialog?.show()

    }

    private fun showNetWorkErrorDialog(){
        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.verify_internet_connection))
            setPositiveButton(getString(R.string.ok)){_, _ ->
                exitActivity()
                setCancelable(false)
            }
        }.create()
        dialog?.show()
    }



    private fun showPaymentReceivedDialog(){
        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.payment_received))
        }.create()
        dialog?.show()
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


        if (dialog != null){
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(this).apply{
            setMessage(resources.getString(R.string.verify_payment_info))
            setView(view)
            setCancelable(false)
            setPositiveButton(resources.getString(R.string.pay)  ){btn, _ ->
                showProcessingRequestDialog()
                viewModel.initiatePayment()
            }
            setNegativeButton(resources.getString(R.string.cancel)){btn, _ ->
                exitActivity()
            }
        }.create()
        dialog?.show()
    }

    private fun showPackagesDialog(){
        if (dialogFragment != null){
            dialogFragment?.dismiss()
        }
        dialogFragment = PackagesDialogFragment.newInstance()
        dialogFragment?.show(supportFragmentManager, null)
    }

    private fun showPaymentMethodDialog() {
        if (dialogFragment != null){
            dialogFragment?.dismiss()
        }
        dialogFragment = PaymentMethodDialogFragment.newInstance()
        dialogFragment?.isCancelable = false
        dialogFragment?.show(supportFragmentManager, null)
    }

    private fun displayEnterMomoNumberDialog(){
        val view = layoutInflater.inflate(R.layout.momo_number_dialog, null)
        val etMoMoNumber: TextInputEditText = view.findViewById(R.id.etMomoNumber)

        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.enter_number))
            setView(view)
            setCancelable(false)
            setPositiveButton( resources.getString(R.string.next)){ _, _ ->
                displayInvoice()
            }
            setNegativeButton(resources.getString(R.string.cancel)){ _, _ ->
                exitActivity()
            }
        }.create()

        dialog?.show()

        val btnPositive = dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
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
        showPaymentMethodDialog()
    }

    override fun onPackageDialogCancelButtonClicked() {
        exitActivity()
    }

    override fun onPackageItemSelected(packageFormData: PackageFormData) {
        viewModel.updateSubscriptionPackageTypePriceAndDuration(packageFormData)
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
        displayEnterMomoNumberDialog()
    }

    override fun onPaymentTypeSelected(momoPartner: String) {
        viewModel.updateMoMoPartner(momoPartner)
    }

    override fun onPaymentMethodCancelButtonClicked(){
        exitActivity()
    }

}