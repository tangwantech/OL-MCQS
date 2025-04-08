package com.example.gceolmcqs

import android.content.pm.PackageManager
import com.parse.ParseObject
import com.parse.ParseQuery

class VersionChecker {
    fun getLatestVersion(onCheckVersionListener: OnCheckVersionListener){
        val parseQuery = ParseQuery.getQuery<ParseObject>(MCQConstants.VERSION_CLASS)
        parseQuery.getInBackground(MCQConstants.APP_VERSION_KEY){parseObject, e ->
            if (e == null){
                val version = parseObject.getString(MCQConstants.VERSION_STR)
                version?.let {
                    onCheckVersionListener.onResult(version)
                }

            }else{
                onCheckVersionListener.onError(e.localizedMessage)
//                    println(e.localizedMessage)
            }
        }
    }

    fun getInstalledVersion(packageManager: PackageManager, packageName: String): String{
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException){
            "Unknown"
        }
    }
    interface OnCheckVersionListener{

        fun onResult(version: String)
        fun onError(error: String?)
    }

}