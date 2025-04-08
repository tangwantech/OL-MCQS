package com.example.gceolmcqs

import android.os.CountDownTimer
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class UsageTimer {
    companion object {
        @Volatile private var sessionTime = 0L
        private var timer: CountDownTimer? = null

        fun startUsageTimer(packageTimeRemaining: Long, countInterval: Long = 1000) {
            timer = object : CountDownTimer(packageTimeRemaining, countInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    updateSessionTime(countInterval)
                }

                override fun onFinish() {
                    stopTimer()
                }
            }.start()
        }

        fun stopTimer() {
            timer?.cancel()
        }

        @Synchronized
        private fun updateSessionTime(checkDuration: Long) {
            sessionTime += checkDuration
        }

        fun getNewBonusTime(oldBonusTime: Long, bonusTimeDiscount: Double): Long {
            return (sessionTime * bonusTimeDiscount).roundToLong() + oldBonusTime
        }

        @Synchronized
        private fun resetSessionTime() {
            sessionTime = 0
        }

        fun resetUsageTimerData() {
            resetSessionTime()
        }

        /**
         * Converts milliseconds to HH:MM:SS format.
         */
        fun formatToHMS(bonusTime: Long): String {
            val hours = TimeUnit.MILLISECONDS.toHours(bonusTime)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(bonusTime) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(bonusTime) % 60

            return when {
                hours > 0 -> String.format("%02d H : %02d M : %02d S", hours, minutes, seconds)
                minutes > 0 -> String.format("%02d M : %02d S", minutes, seconds)
                seconds > 0 -> String.format("%02d S", seconds)
                else -> "NA"
            }
        }

        /**
         * Checks if there is bonus time left.
         */
        fun isBonusTimeAvailable(bonusTime: Long): Boolean {
            return bonusTime > 0
        }


    }
}
