package com.example.gceolmcqs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gceolmcqs.SpinToEarnTime
import com.example.gceolmcqs.SpinToEarnTime.SpinToEarnTimeListener

class SpinActivityViewModel: ViewModel() {
    private lateinit var spinToEarnTime: SpinToEarnTime

    private val _packages = MutableLiveData<ArrayList<Int>>()
    val packages: LiveData<ArrayList<Int>> = _packages
    fun initSpinToEarnTime(currentPackageType: String){
        val packageBonuses = hashMapOf("12H" to arrayListOf(0, 30, 45, 5, 90, 120, 15, 60), "24H" to arrayListOf(0, 45, 90, 120, 140, 200, 15, 60))
        val temp = ArrayList<Int>()
        temp.addAll(packageBonuses[currentPackageType]!!.toList())
        temp.sort()
        println(temp)
        _packages.value = temp
        spinToEarnTime = SpinToEarnTime().apply {
            updateAvailableBonuses(temp)
        }
    }



    fun spin(spinToEarnTimeListener: SpinToEarnTimeListener){
        spinToEarnTime.spin(spinToEarnTimeListener)
    }

    fun getSpinBonusTimeAndLuckyAngle(): Pair<Int, Float>{
        return spinToEarnTime.getSpinBonusTimeAndLuckyAngle()
    }
}