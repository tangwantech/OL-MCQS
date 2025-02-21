package com.example.gceolmcqs

import com.example.gceolmcqs.datamodels.ActivationExpiryDates
import java.util.*
import kotlin.math.roundToInt

class ActivationExpiryDatesGenerator {

   fun checkExpiry(activatedOn: String, expiresOn: String): Boolean{
        val currentDate = Date()
        val activationDate = Date(Date.parse(activatedOn))
        val expiryDate = Date(Date.parse(expiresOn))
        return currentDate > activationDate && currentDate < expiryDate
   }
//    fun checkGraceExtensionExpiry(activatedOn: String, expiresOn: String): Boolean{
//        val currentDate = Date()
//        val activationDate = Date(Date.parse(activatedOn))
//        val expiryDate = Date(Date.parse(expiresOn))
//        return currentDate < expiryDate
//    }

    companion object {
        const val SECONDS = "seconds"
        const val MINUTES = "minutes"
        const val HOURS = "hours"
        const val DAYS = "days"
        fun generateActivationExpiryDates(timeType: String=MCQConstants.HOURS, duration: Int): ActivationExpiryDates {

            val activationDate = Date()
            val expiry = Date()

            when(timeType){
                SECONDS -> {
                    expiry.seconds = expiry.seconds.plus(duration)
                }
                MINUTES -> {
                    expiry.minutes = expiry.minutes.plus(duration)
                }

                HOURS -> {
                    expiry.hours = expiry.hours.plus(duration)
                }

            }

            return ActivationExpiryDates( activationDate.toLocaleString(), expiry.toLocaleString())
        }

        fun getTimeRemaining(activatedOn: String, expiresOn: String): Long{
            val dateNow = Date()
            val activationDate = Date(activatedOn)
            val expiry = Date(expiresOn)
            return if(dateNow < activationDate || dateNow > expiry){
                0
            }else{
//                expiry.time - activationDate.time
                expiry.time - dateNow.time
            }

        }

        fun getGraceActivatedAndExpiryDate(oldDate: String, packageType: String = MCQConstants.MCQ_DAY): ActivationExpiryDates{
            val expiry = Date(Date.parse(oldDate))
            var tempDuration = 0
            when (packageType){
                MCQConstants.TRIAL -> {
                    tempDuration = (MCQConstants.TRIAL_DURATION * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                }

                MCQConstants.MCQ_DAY ->{
                    tempDuration = (24 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                    println(tempDuration)
                }
                MCQConstants.MCQ_WEEK ->{
                    tempDuration = (168 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                }
                MCQConstants.MCQ_MONTH ->{
                    tempDuration = (720 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                }
            }
            expiry.hours = expiry.hours.plus(tempDuration)
            return ActivationExpiryDates( oldDate, expiry.toLocaleString())
        }

        fun getNewExpiryDate(oldDate: String, duration: Long, timeType: String = SECONDS): String{
            val expiry = Date(Date.parse(oldDate))
            val tempDuration = (duration / 1000).toInt()
            when (timeType){
                SECONDS -> {
                    expiry.seconds = expiry.seconds.plus(tempDuration)
                }
            }

            return expiry.toLocaleString()
        }

    }


}