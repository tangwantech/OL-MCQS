package com.example.gceolmcqs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.gceolmcqs.adapters.HomeRecyclerViewAdapter
import com.example.gceolmcqs.databinding.ActivityMainBinding

import com.example.gceolmcqs.repository.RemoteRepoManager
import com.example.gceolmcqs.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(),
    HomeRecyclerViewAdapter.OnHomeRecyclerItemClickListener,
        HomeRecyclerViewAdapter.OnPackageExpireListener
{

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeRecyclerViewAdapter: HomeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("Main", MODE_PRIVATE)
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setupViewModel()
        setupRecyclerView()


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
            this, this)
        binding.homeRecyclerView.adapter = homeRecyclerViewAdapter
    }

    private fun gotoSubjectContentTableActivity(position: Int) {
        val intent = Intent(this, SubjectContentTableActivity::class.java)

        intent.apply {
            putExtra(MCQConstants.SUBJECT_INDEX, position)
        }
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

    private fun privacyPolicy() {
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

    private fun gotoAboutUs(){
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun gotoTermsOfServiceActivity(){
        startActivity(TermsOfServiceActivity.getIntent(this))
    }


    override fun onResume() {
        super.onResume()
        viewModel.updateSubjectPackageDataList()
        homeRecyclerViewAdapter.notifyDataSetChanged()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            android.R.id.home ->{
//                showExitDialog()
//            }

            R.id.share -> {
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                shareApp()
            }
            R.id.rateUs -> {
                rateUs()
            }
            R.id.terms -> {
                gotoTermsOfServiceActivity()
            }

            R.id.privacyPolicy -> {
                privacyPolicy()
            }
            R.id.about -> {
                gotoAboutUs()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
//        super.onBackPressed()
//        super.onBackPressed()
        showExitDialog()

    }


    override fun onSubjectItemClicked(position: Int, isPackageActive: Boolean?, packageName: String?) {
        if(packageName == MCQConstants.NA){
            Toast.makeText(this, "Please activate your Trial Package", Toast.LENGTH_LONG).show()
        }else{
            isPackageActive?.let{
                if (it) {
                    gotoSubjectContentTableActivity(position)

                } else {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.apply {
                        setMessage(resources.getString(R.string.package_expired_message))
                        setPositiveButton("Ok") { _, _ ->

                        }
                    }.create().show()
                }
            }

        }

    }

    override fun onSubscribeButtonClicked(position: Int, subjectName: String) {
//        setSubjectPackageDataToActivate(position, subjectPackageData)
        gotoSubscriptionActivity(position, subjectName)
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
//                homeRecyclerViewAdapter.notifyItemChanged(index)
            }

            override fun onError() {

            }

        })

    }


}
