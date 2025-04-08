package com.example.gceolmcqs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.text.HtmlCompat
import com.example.gceolmcqs.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.about)

        setupListener()
        setVersionNumber()
        setCopyWrite()


    }

    private fun setVersionNumber(){
        val versionName = VersionChecker().getInstalledVersion(packageManager, packageName)
        binding.tvVersion.text = getString(R.string.version, versionName)
    }

    private fun setCopyWrite(){
        val tag = "<p>&copy; ${getString(R.string.currentYear)} ${getString(R.string.company)}</p>"
        binding.tvCopyWrite.text = HtmlCompat.fromHtml(tag, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListener(){
        binding.btnTerms.setOnClickListener {
            gotoTermsOfServiceActivity()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            gotoPrivacyPolicy()
        }
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
}