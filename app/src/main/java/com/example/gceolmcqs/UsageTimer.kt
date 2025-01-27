package com.example.gceolmcqs

import android.os.CountDownTimer

class UsageTimer {
    companion object{
        private var spinPoints = 0
        private var sessionTime = 0L
        private var cumulatedTime = 0L
        private var timer: CountDownTimer? = null

        fun startTimer(oldCumulatedTime: Long, spinPoints: Int, packageTimeRemaining: Long, countInterval: Long, timeToEarnPoints: Long){
           initCumulatedTimeAndSpinPoints(oldCumulatedTime, spinPoints)
            timer = object :CountDownTimer(packageTimeRemaining, countInterval){
                override fun onTick(p0: Long) {
                    updateSessionTime(countInterval)
                    updateCumulatedTime(oldCumulatedTime)
                    calculateSpinPoints(timeToEarnPoints)
                }

                override fun onFinish() {
                    calculateSpinPoints(timeToEarnPoints)
                }

            }.start()
        }

        fun stopTimer(){
            timer?.cancel()
        }

        private fun calculateSpinPoints(timeToEarnPoints: Long){
            val tempSpinPoint  = ((this.cumulatedTime / timeToEarnPoints)).toInt()
            this.spinPoints += tempSpinPoint
            println("SpinPoints: ${this.spinPoints}")
            val remainingTime = this.cumulatedTime % timeToEarnPoints
            if(tempSpinPoint > 0){
                resetSessionTime()
                updateCumulatedTime(remainingTime)
            }

        }

        private fun initCumulatedTimeAndSpinPoints(cumulatedTime: Long, spinPoints: Int){
            this.cumulatedTime = cumulatedTime
            this.spinPoints = spinPoints
        }

        private fun updateSessionTime(checkDuration: Long){
            this.sessionTime += checkDuration
            println("SessionTime: ${this.sessionTime}")
        }

        private fun updateCumulatedTime(cumulatedTime: Long){
            this.cumulatedTime = sessionTime + cumulatedTime
            println("Cumulated Time: ${this.cumulatedTime}")
        }

        private fun resetSessionTime(){
            this.sessionTime = 0
        }

        private fun resetCumulatedTime(){
            this.cumulatedTime = 0
            println("Cumulated Time reset: ${this.cumulatedTime}")
        }

        fun resetUsageTimerData(){
            resetSessionTime()
            resetCumulatedTime()
        }
    }
}