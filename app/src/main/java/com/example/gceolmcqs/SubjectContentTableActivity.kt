package com.example.gceolmcqs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.gceolmcqs.adapters.SubjectContentTableViewPagerAdapter

import com.example.gceolmcqs.datamodels.SubjectPackageData
import com.example.gceolmcqs.fragments.ExamTypeFragment
import com.example.gceolmcqs.viewmodels.SubjectContentTableViewModel
import com.google.android.material.tabs.TabLayout


class SubjectContentTableActivity : AppCompatActivity(),
    ExamTypeFragment.OnPackageExpiredListener,
    ExamTypeFragment.OnContentAccessDeniedListener{

    private lateinit var viewModel: SubjectContentTableViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var pref: SharedPreferences
    private var currentTabIndex  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_content_table)
        pref = getSharedPreferences(SUBJECT_CONTENT_TABLE, MODE_PRIVATE)
        setAlertDialog()
        initActivityViews()
        initViewModel()
        setupActivityViewListeners()
        setupViewObservers()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }
    private fun initActivityViews(){
        tabLayout = findViewById(R.id.homeTab)
        viewPager = findViewById(R.id.homeViewPager)
    }

    private fun initViewModel(){

        viewModel = ViewModelProvider(this)[SubjectContentTableViewModel::class.java]
        viewModel.setSubjectIndex(intent.getIntExtra(MCQConstants.SUBJECT_INDEX, 0))
//        viewModel.setSubjectName(subjectTitle!!)


    }

    private fun setupViewObservers(){
        viewModel.getIsPackageActive().observe(this, Observer {
            if (!it) {
                showAlertDialog()
            }
        })

        viewModel.subjectPackageData.observe(this, Observer{ subjectPackageData ->
            setUpSubjectContentTab(subjectPackageData)
        })
    }

    private fun setAlertDialog(){
        alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setMessage(resources.getString(R.string.package_expired_message))
            setPositiveButton("Ok") { _, _ ->
                exitActivity()
            }
            setCancelable(false)
        }.create()
    }

    private fun showAlertDialog(){
        alertDialog.show()
    }

    private fun exitActivity() {
        this.finish()
    }

    private fun setUpSubjectContentTab(subjectPackageData: SubjectPackageData) {
        val subjectIndex = intent.getIntExtra(MCQConstants.SUBJECT_INDEX, 0)

        val tabIndex = pref.getInt(TAB_INDEX, 0)
        val tabFragments: ArrayList<Fragment> = ArrayList()

        for (fragmentIndex in 0 until viewModel.getExamTypesCount()) {
            val fragment =
                ExamTypeFragment.newInstance(
                    fragmentIndex,
                    viewModel.getSubjectName(),
                    subjectPackageData.expiresOn!!,
                    subjectPackageData.packageName!!,
                    subjectIndex

                )
            tabFragments.add(fragment)
        }

        val viewPagerAdapter = SubjectContentTableViewPagerAdapter(
            this.supportFragmentManager,
            tabFragments,
            viewModel.getExamTitles()
        )
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = tabIndex
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setupActivityViewListeners(){

        tabLayout.setOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position!!
                saveSelectedTab(currentTabIndex)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        title = viewModel.getSubjectName()
        viewModel.loadSubjectPackageDataFromRemoteRepoAtIndex(intent.getIntExtra(MCQConstants.SUBJECT_INDEX, 0))
    }

    override fun onDestroy() {
        saveSelectedTab(0)
        super.onDestroy()

    }

    override fun onShowPackageExpired() {
        showAlertDialog()
    }

    override fun onCheckIfPackageHasExpired(): Boolean {
//        return viewModel.getPackageStatus()
        return true
    }

    override fun onContentAccessDenied() {
        val contentAccessDeniedDialog = AlertDialog.Builder(this)
        contentAccessDeniedDialog.apply {
            setMessage(resources.getString(R.string.content_access_denied_Message))
            setPositiveButton("Ok") { d, _->
                d.dismiss()
            }
        }.create().show()
    }

    private fun saveSelectedTab(index: Int){
        pref.edit().apply {
            putInt(TAB_INDEX, index)
        }.apply()
    }

    companion object{
        private const val SUBJECT_CONTENT_TABLE = "subject content table"
        private const val TAB_INDEX = "tab index"
    }
}

