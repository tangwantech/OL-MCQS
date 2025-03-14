package com.example.gceolmcqs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.gceolmcqs.adapters.HomeRecyclerViewAdapter
import com.example.gceolmcqs.databinding.ActivityMainBinding

import com.example.gceolmcqs.repository.RemoteRepoManager
import com.example.gceolmcqs.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(),
    HomeRecyclerViewAdapter.OnHomeRecyclerItemListener
{

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeRecyclerViewAdapter: HomeRecyclerViewAdapter
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("Main", MODE_PRIVATE)
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setupViewModel()
        setupRecyclerView()
        setupObservers()
//        setupAppUsageReminderSharedPreference()
//        startReminderService()



    }

    private fun startReminderService(){
        val serviceIntent = Intent(this, AppReminderService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

    }

    private fun setupAppUsageReminderSharedPreference(){
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(MCQConstants.APP_USAGE_PREFS, MODE_PRIVATE)
        sharedPreferences.edit().putLong(MCQConstants.LAST_USED, System.currentTimeMillis()).apply()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.updateSubjectPackageDataList()
        viewModel.initAppData()
//
    }

    private fun setupRecyclerView(){
//        Displays list of subject packages available
        val loMan = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.homeRecyclerView.layoutManager = loMan
        binding.homeRecyclerView.setHasFixedSize(true)

        homeRecyclerViewAdapter = HomeRecyclerViewAdapter(
            this,
            viewModel.getSubjectPackageDataList(),
            this)
        binding.homeRecyclerView.adapter = homeRecyclerViewAdapter
    }

    private fun setupObservers(){
        viewModel.usageTimeBonus.observe(this){
            val subjectIndex = viewModel.getIndexOfCurrentSubject()
            saveUsageBonusTime(it, subjectIndex)
        }
    }

    private fun gotoSubjectContentTableActivity(position: Int) {
        val intent = Intent(this, SubjectContentTableActivity::class.java)

        intent.apply {
            putExtra(MCQConstants.SUBJECT_INDEX, position)
        }
        startActivity(intent)
    }

    private fun gotoSpinActivity(position: Int){
        val intent = SpinActivity.getIntent(this, position)
        startActivity(intent)
    }

    private fun shareApp() {
//        val uri = Uri.parse(MCQConstants.APP_URL)
        val appMsg = "${resources.getString(R.string.share_message)}\nLink: ${MCQConstants.APP_URL}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = MCQConstants.TYPE
        intent.putExtra(Intent.EXTRA_TEXT, appMsg)
        startActivity(intent)
    }

    private fun rateUs() {
        val uri = Uri.parse(MCQConstants.APP_URL)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MCQConstants.APP_URL)))
        }
    }

    private fun updateAppData(){

        checkingForUpdateDialog()
        viewModel.updateAppData(object: AppDataUpdater.AppDataUpdateListener{
            override fun onAppDataUpdated() {
//                NetworkTimeout.stopTimer()
//                checkingDialog.dismiss()
                displayAppDataUpDatedDialog()
            }

            override fun onError() {
//                NetworkTimeout.stopTimer()
                displayErrorDialog(getString(R.string.network_timeout))
            }

            override fun onAppDataUpToDate() {
//                NetworkTimeout.stopTimer()
//                checkingDialog.dismiss()
                displayAppDataIsUpToDateDialog()
            }
        })

//        NetworkTimeout.checkTimeout(MCQConstants.NETWORK_TIME_OUT_DURATION, object: NetworkTimeout.OnNetWorkTimeoutListener{
//            override fun onNetworkTimeout() {
////                checkingDialog.dismiss()
//                displayErrorDialog(getString(R.string.network_timeout))
//            }
//        })
    }

    private fun displayErrorDialog(message: String){
        if (dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(getString(R.string.ok)){d, _ ->
                d.dismiss()
            }
        }.create()
        dialog?.show()
    }

    private fun  displayAppDataIsUpToDateDialog(){
        if(dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage("App data is up to date.")
            setPositiveButton(getString(R.string.ok)){d, _ ->
                d.dismiss()
            }
        }.create()
        dialog?.show()
    }

    private fun checkingForUpdateDialog(){
        if(dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.checking_for_latest_update))
            setCancelable(false)
        }.create()
        dialog?.show()
    }

    private fun displayAppDataUpDatedDialog(){
        if(dialog != null){
            dialog?.dismiss()
        }
        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.app_data_updated_successfully))
            setPositiveButton(getString(R.string.exit)){_, _ ->
                finish()
            }
            setCancelable(false)
        }.create()
        dialog?.show()
    }



    private fun gotoAboutUs(){
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

//    private fun gotoTermsOfServiceActivity(){
//        startActivity(TermsOfServiceActivity.getIntent(this))
//    }

    private fun setTitle(){
        title = ""
    }

    override fun onResume() {
        super.onResume()
        setTitle()
        viewModel.updateSubjectPackageDataList()
        updateUsageBonusTime()
        homeRecyclerViewAdapter.notifyDataSetChanged()

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.share -> {
//
                shareApp()
            }
            R.id.rateUs -> {
                rateUs()
            }
            R.id.about -> {
                gotoAboutUs()
            }
            R.id.updateAppData -> {
                updateAppData()
            }
            R.id.exit -> {
                showExitDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        showExitDialog()

    }


    override fun onSubjectItemClicked(position: Int, isPackageActive: Boolean?, packageName: String?) {
        setIndexOfCurrentSubject(position)
        if(packageName == MCQConstants.NA){
//            Toast.makeText(this, "Please activate your Trial Package", Toast.LENGTH_LONG).show()
        }else{
            isPackageActive?.let{
                gotoSubjectContentTableActivity(position)
//                if (it) {
//                    gotoSubjectContentTableActivity(position)
//
//                } else {
//                    val alertDialog = AlertDialog.Builder(this)
//                    alertDialog.apply {
//                        setMessage(resources.getString(R.string.package_expired_message))
//                        setPositiveButton("Ok") { _, _ ->
//
//                        }
//                    }.create().show()
//                }
            }

        }

    }

    override fun onSubscribeButtonClicked(position: Int, subjectName: String) {
//        setSubjectPackageDataToActivate(position, subjectPackageData)
        gotoSubscriptionActivity(position, subjectName)
    }

    override fun onActivateBonusButtonClicked(position: Int, subjectName: String, isActive: Boolean) {
//        println("Subject index: $position, Subject: $subjectName")

        activateBonus(position, isActive)
        displayDialogActivatingBonus()
    }


    private fun gotoSubscriptionActivity(subjectIndex: Int, subjectName: String){
        startActivity(SubscriptionActivity.getIntent(this, subjectIndex, subjectName))
    }


    private fun showExitDialog() {
        val dialogExit = AlertDialog.Builder(this)
        dialogExit.apply {
            setMessage(getString(R.string.exit_message))
            setNegativeButton(resources.getString(R.string.cancel)) { p, _ ->
                p.dismiss()
            }
            setPositiveButton(resources.getString(R.string.exit)) { _, _ ->
                this@MainActivity.finish()
            }
            setCancelable(false)
        }.create().show()
    }

    override fun onPackageExpired(index: Int) {
        viewModel.updatePackageStatusAt(index, object : RemoteRepoManager.OnUpdatePackageListener{
            override fun onUpDateSuccessful(index: Int) {
                homeRecyclerViewAdapter.notifyItemChanged(index)
            }

            override fun onError() {

            }

        })

    }


    override fun onUsageBonusAvailable(subjectIndex: Int): Long {
        val temp = pref.getLong("$subjectIndex", 0)
        return temp
    }

    private fun setIndexOfCurrentSubject(position: Int){
        viewModel.setIndexOfCurrentSubject(position)
        viewModel.resetUsageTimer()
    }

    private fun updateUsageBonusTime(){
        val subjectIndex = viewModel.getIndexOfCurrentSubject()
        subjectIndex?.let{
            val oldBonus = pref.getLong("$it", 0)
            viewModel.calculateNewBonusTime(oldBonus, MCQConstants.BONUS_TIME_DISCOUNT)
            updateBonusTimeInRecyclerAdapter(oldBonus, subjectIndex)
        }


    }

    private fun saveUsageBonusTime(bonusTime: Long, subjectIndex: Int?){
        subjectIndex?.let {
            pref.edit().apply {
                putLong("$it", bonusTime)
            }.apply()
            updateBonusTimeInRecyclerAdapter(bonusTime, it)
        }



    }

    private fun updateBonusTimeInRecyclerAdapter(bonusTime: Long, subjectIndex: Int){
        homeRecyclerViewAdapter.updateBonusTime(bonusTime)
        homeRecyclerViewAdapter.notifyItemChanged(subjectIndex)
    }

    private fun consumeBonusTime(subjectIndex: Int){
        saveUsageBonusTime(0L, subjectIndex)
        viewModel.resetUsageTimer()

    }

    private fun activateBonus(subjectIndex: Int, isActive: Boolean){
        val bonusTime = pref.getLong("$subjectIndex", 0)
        viewModel.extentSubjectPackageAt(subjectIndex, bonusTime, isActive, object: RemoteRepoManager.OnUpdatePackageListener{
            override fun onUpDateSuccessful(index: Int) {
                displayDialogBonusActivated(index)
            }

            override fun onError() {
//
                displayDialogFailToActivateBonus()

            }

        })
    }

    private fun displayDialogActivatingBonus(){
        if (dialog != null){
            dialog?.dismiss()
        }
        val view = LayoutInflater.from(this).inflate(R.layout.circular_progress_bar, null)
        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.activating_bonus))
            setView(view)
            setCancelable(false)
        }.create()
        dialog?.show()
    }
    private fun displayDialogBonusActivated(subjectIndex: Int){
        if (dialog != null){
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.bonus_activated))
            setPositiveButton(getString(R.string.ok)){_, _ ->
                consumeBonusTime(subjectIndex)
            }
            setCancelable(false)
        }.create()
        dialog?.show()
    }

    private fun displayDialogFailToActivateBonus(){
        if (dialog != null){
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.failed_to_activate_bonus))
            setPositiveButton(getString(R.string.ok)){_, _ ->

            }
            setCancelable(false)
        }.create()
        dialog?.show()
    }

}
