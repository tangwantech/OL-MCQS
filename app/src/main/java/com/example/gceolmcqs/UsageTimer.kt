package com.example.gceolmcqs

import android.os.CountDownTimer
import android.text.format.Time
import kotlin.math.roundToLong

class UsageTimer {
    companion object{

        private var sessionTime = 0L
        private var timer: CountDownTimer? = null

        fun startUsageTimer(packageTimeRemaining: Long, countInterval: Long = 1000){
            timer = object :CountDownTimer(packageTimeRemaining, countInterval){
                override fun onTick(p0: Long) {
                    updateSessionTime(countInterval)
                }

                override fun onFinish() {
                }

            }.start()
        }


        fun stopTimer(){
            timer?.cancel()
        }


        private fun updateSessionTime(checkDuration: Long){
            this.sessionTime += checkDuration
        }

        fun getNewBonusTime(oldBonusTime: Long, bonusTimeDiscount: Double): Long{

            val newBonusTime = (sessionTime * bonusTimeDiscount).roundToLong() + oldBonusTime
            return newBonusTime
        }

        private fun resetSessionTime(){
            this.sessionTime = 0
        }

        fun resetUsageTimerData(){
            resetSessionTime()
        }

        fun formatToHMS(bonusTime: Long): String{
            val timeLeft = Time().apply { set(bonusTime) }
            var timeRemainingInSeconds = timeLeft.toMillis(true) / 1000
            val secondsInHours = 3600
            val secondsInMinutes = 60

            val hoursRemaining = timeRemainingInSeconds / secondsInHours
            timeRemainingInSeconds %= secondsInHours
            val minutesRemaining = timeRemainingInSeconds / secondsInMinutes
            timeRemainingInSeconds %= secondsInMinutes

            return if(hoursRemaining > 0){
                "${hoursRemaining.toString().padStart(2, '0')} H : ${
                    minutesRemaining.toString().padStart(2, '0')
                } M : ${timeRemainingInSeconds.toString().padStart(2, '0')} S"
            }else if (minutesRemaining > 0){
                "${minutesRemaining.toString().padStart(2, '0')
                } M : ${timeRemainingInSeconds.toString().padStart(2, '0')} S"
            }else if (timeRemainingInSeconds > 0){
                "${timeRemainingInSeconds.toString().padStart(2, '0')} S"

            }else{
                "NA"
            }
        }

        fun isBonusTimeAvailable(bonusTime: Long): Boolean{
            val temp = (bonusTime / 1000) / 60
            return temp > 0
        }

    }

}