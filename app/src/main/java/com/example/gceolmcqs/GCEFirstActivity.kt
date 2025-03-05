package com.example.gceolmcqs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gceolmcqs.viewmodels.SplashActivityViewModel
import com.example.gceolmcqs.databinding.ActivitySplashBinding
import com.example.gceolmcqs.databinding.TermsOfUseLayoutBinding
import com.example.gceolmcqs.repository.RemoteRepoManager
import com.parse.ParseException
import kotlinx.coroutines.*

class GCEFirstActivity : AppCompatActivity() {
    private val serverRetryLimit = 2
    private lateinit var viewModel: SplashActivityViewModel
    private lateinit var pref: SharedPreferences
    private var termsOfServiceDialog: AlertDialog? = null
    private var initializingAppDialog: AlertDialog? = null
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)

//        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        setupViewModel()
        setupObservers()
        val termsAccepted = pref.getBoolean(MCQConstants.TERMS_ACCEPTED, false)
        if(!termsAccepted){
            binding.loProgressBar.visibility = View.GONE
            displayTermsOfServiceDialog()
        }else{
            verifyDeviceIdInAppDatabase()
        }

    }

    private fun verifyDeviceIdInAppDatabase(){
        NetworkTimeout.checkTimeout(MCQConstants.NETWORK_TIME_OUT_DURATION, object: NetworkTimeout.OnNetWorkTimeoutListener{
            override fun onNetworkTimeout() {
                displayErrorDialog(getString(R.string.network_timeout))
            }
        })
        viewModel.verifyDeviceIdInAppDatabase(object: RemoteRepoManager.OnDeviceDataExistsListener{
            override fun onDeviceDataExists() {
                val isAvailable = viewModel.verifyAppDataAvailability()
//                gotoMainActivity()
                if (isAvailable){
                    NetworkTimeout.stopTimer()
                    gotoMainActivity()
                }else{
                    viewModel.getAppData(object: RemoteRepoManager.OnAppDataAvailableListener{
                        override fun onAppDataAvailable() {
                            NetworkTimeout.stopTimer()
                            gotoMainActivity()
                        }

                        override fun onError(e: ParseException) {
                            println("exception raised: ${e.localizedMessage}")
                            e.localizedMessage?.let { displayErrorDialog(it)}
                        }

                    })
                }
            }

            override fun onError(e: ParseException) {
//                e.localizedMessage?.let { displayErrorDialog(it)}
            }
        })

    }

    fun displayErrorDialog(error: String){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(error)
            setNegativeButton("Exit"){_, _ ->
                finish()
            }

        }.create()
        alertDialog.show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[SplashActivityViewModel::class.java]
    }


    private fun setupObservers(){

    }


    private fun displayInternetConnectionDialog(){
        displayTermsOfServiceDialog()
    }

    private fun displayTermsOfServiceDialog(){
//        val view = LayoutInflater.from(this).inflate(R.layout.terms_of_use_layout, null)
        val dialogBinding = TermsOfUseLayoutBinding.inflate(layoutInflater)
        dialogBinding.btnTerms.setOnClickListener {
            gotoTermsOfServiceActivity()
        }

       dialogBinding.btnPrivacyPolicy.setOnClickListener {
            gotoPrivacyPolicy()
        }


        termsOfServiceDialog = AlertDialog.Builder(this).create()
        termsOfServiceDialog?.setTitle(resources.getString(R.string.agreement))
        termsOfServiceDialog?.setView(dialogBinding.root)
        termsOfServiceDialog?.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.accept)) { _, _ ->
            binding.loProgressBar.visibility = View.VISIBLE
            saveTermsOfServiceAcceptedStatus()
            verifyDeviceIdInAppDatabase()
        }
        termsOfServiceDialog?.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.decline)) { _, _ ->
            finish()
        }
        termsOfServiceDialog?.setCancelable(false)
        termsOfServiceDialog?.show()
    }

    private fun gotoTermsOfServiceActivity(){
        startActivity(TermsOfServiceActivity.getIntent(this))
    }

    private fun gotoPrivacyPolicy() {
        val uri = Uri.parse(MCQConstants.PRIVACY_POLICY)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MCQConstants.PRIVACY_POLICY)))
        }
    }

    private fun errorDialog(errorMessage: String){
        val timeoutDialog = AlertDialog.Builder(this).apply {
            setMessage(errorMessage)
            setNegativeButton("Exit"){_, _ ->
                finish()
            }
        }.create()
        timeoutDialog.show()
    }


    private fun gotoMainActivity(){
        CoroutineScope(Dispatchers.IO).launch{
            delay(2000L)
            withContext(Dispatchers.Main){
                val intent = Intent(this@GCEFirstActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
//
            }
        }
    }

//    private fun getJsonFromAssets(): String? {
//        val charset: Charset = Charsets.UTF_8
//
//        return try {
//            val jsonFile = assets.open("subject_data.json")
//            val size = jsonFile.available()
//            val buffer = ByteArray(size)
//
//            jsonFile.read(buffer)
//            jsonFile.close()
//            String(buffer, charset)
//
//        } catch (e: IOException) {
//            null
//        }
//    }

    private fun saveTermsOfServiceAcceptedStatus(){
        pref.edit().apply {
            putBoolean(MCQConstants.TERMS_ACCEPTED, true)
            apply()
        }
    }



}