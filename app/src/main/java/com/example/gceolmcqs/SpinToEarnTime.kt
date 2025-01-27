package com.example.gceolmcqs

import android.os.CountDownTimer
import kotlin.random.Random

class SpinToEarnTime(private var previousBonus:Long = 0, private var spinPoints: Int=0){
    private var bonusTime = 0
    private var spinAngleChange = 0F
    private var luckyAngle = 0F
    private val availableBonuses = ArrayList<Int>()
    val maxAngle = 360f
    var accruedBonuses = previousBonus

    fun updateAvailableBonuses(availableBonuses: List<Int>){
        this.availableBonuses.addAll(availableBonuses)
//        this.availableBonuses.shuffled()
        setSpinAngleChange()
    }

    private fun setSpinAngleChange(){
        this.spinAngleChange = (maxAngle / availableBonuses.size).toFloat()
    }

    private fun decrementSpinPoint(){
        spinPoints -= 1
    }

    fun spin(spinToEarnTimeListener: SpinToEarnTimeListener){
        val randomIndex = Random.nextInt(availableBonuses.size)
        bonusTime = availableBonuses[randomIndex]
        decrementSpinPoint()
        spinCountDown(spinAngleChange, 20, spinToEarnTimeListener)

    }

    private fun spinCountDown(spinAngleChange: Float, interval: Long, spinToEarnTimeListener: SpinToEarnTimeListener){
        val tempDuration = Random.nextInt(5000, 9000).toLong()
//        println("Duration: $tempDuration")
        val timer = object: CountDownTimer(tempDuration, interval){
            var angle = 0f
            override fun onTick(p0: Long) {
                angle += spinAngleChange
                if(angle >= maxAngle){
                    angle = 0f
                }
                spinToEarnTimeListener.onSpinAngleChange(angle)
            }

            override fun onFinish() {
                val index = (angle / spinAngleChange).toInt()
                println("angle: $angle, current bonus:${availableBonuses[index]}, index: $index")
                val bonusInMinutes = availableBonuses[index]
                val currentBonusEarnedInMilliSecs = (availableBonuses[index] * 60 * 1000).toLong()
                accruedBonuses += currentBonusEarnedInMilliSecs
                spinToEarnTimeListener.onSpinAngleChange(angle)
                spinToEarnTimeListener.bonusesAvailable(currentBonusEarnedInMilliSecs, bonusInMinutes,  accruedBonuses)
                spinToEarnTimeListener.spinPointsRemaining(spinPoints)

            }

        }
        timer.start()
    }

    fun getSpinBonusTimeAndLuckyAngle(): Pair<Int, Float>{
        return Pair(bonusTime, luckyAngle)
    }

    fun getSpinPoints(): Int{
        return spinPoints
    }

    fun getSpinAngleChange(): Float{
        return spinAngleChange
    }

    fun getAccumulatedBonus(): Long{
        return bonusTime.toLong() + previousBonus
    }

    interface SpinToEarnTimeListener{
        fun onSpinAngleChange(angle: Float)
        fun bonusesAvailable(currentBonusEarned: Long, bonusInMinutes: Int, accruedBonus: Long)
        fun spinPointsRemaining(spinPoints: Int)
    }

}