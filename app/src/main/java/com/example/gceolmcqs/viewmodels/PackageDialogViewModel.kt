package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.datamodels.PackageFormData
import com.example.gceolmcqs.datamodels.PackagesData
import com.google.gson.Gson

class PackageDialogViewModel: ViewModel() {
    private val packages = ArrayList<PackageFormData>()
//    private var selectedPackage: PackageData? = null

    fun setPackages(jsonData: String){

        val packagesData = Gson().fromJson(jsonData, PackagesData::class.java)
        packages.addAll(packagesData.packages)

    }

    fun getPackages(): ArrayList<PackageFormData>{
        return packages
    }

    fun updatePackageDataAt(position: Int, isChecked: Boolean){
        for (index in 0 until packages.size){
            if( index == position){
                packages[index].isChecked = isChecked
            }else{
                packages[index].isChecked = false
            }
        }
//        println(packages)
    }

    fun getSubjectPackageAtIndex(index: Int): PackageFormData{
        return packages[index]
    }

//    fun getSelectedPackage(): PackageData{
//        return packages.first { it.isChecked }
//    }

    fun clearPackages(){
        packages.clear()
    }

}