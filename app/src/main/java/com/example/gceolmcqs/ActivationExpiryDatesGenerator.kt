package com.example.gceolmcqs

import com.example.gceolmcqs.datamodels.ActivationExpiryDates
import java.util.*
import kotlin.math.roundToInt
import java.text.SimpleDateFormat


class ActivationExpiryDatesGenerator {

    fun checkExpiry(activatedOn: String, expiresOn: String): Boolean {
        val currentDate = Date()
        val activationDate = parseDate(activatedOn)
        val expiryDate = parseDate(expiresOn)

        return currentDate.after(activationDate) && currentDate.before(expiryDate)
    }

    companion object {
        private const val SECONDS = "seconds"
        private const val MINUTES = "minutes"
        private const val HOURS = "hours"
        private const val DAYS = "days"

        // Standard Date Format (Ensures consistency)
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

        /**
         * Parses a date string, ensuring it's always in the correct format.
         */
        private fun parseDate(dateString: String): Date {
            return try {
                dateFormat.parse(dateString) ?: Date()
            } catch (e: Exception) {
                // If parsing fails, return the current date
                e.printStackTrace()
                Date()
            }
        }

        /**
         * Generates activation and expiry dates based on duration.
         */
        fun generateActivationExpiryDates(timeType: String = HOURS, duration: Int): ActivationExpiryDates {
            val activationDate = Date()
            val expiry = Calendar.getInstance()

            when (timeType) {
                SECONDS -> expiry.add(Calendar.SECOND, duration)
                MINUTES -> expiry.add(Calendar.MINUTE, duration)
                HOURS -> expiry.add(Calendar.HOUR, duration)
            }

            return ActivationExpiryDates(
                dateFormat.format(activationDate),  // Always store dates in a standard format
                dateFormat.format(expiry.time)
            )
        }

        /**
         * Returns the time remaining before expiry.
         */
        fun getTimeRemaining(activatedOn: String, expiresOn: String): Long {
            val dateNow = Date()
            val activationDate = parseDate(activatedOn)
            val expiry = parseDate(expiresOn)

            return if (dateNow.before(activationDate) || dateNow.after(expiry)) {
                0
            } else {
                expiry.time - dateNow.time
            }
        }

        /**
         * Computes grace period expiry date.
         */
        fun getGraceActivatedAndExpiryDate(oldDate: String, packageType: String = MCQConstants.MCQ_DAY): ActivationExpiryDates {
            val expiry = Calendar.getInstance().apply {
                time = parseDate(oldDate)
            }
            val tempDuration = when (packageType) {
                MCQConstants.TRIAL -> (MCQConstants.TRIAL_DURATION * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                MCQConstants.MCQ_DAY -> (24 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                MCQConstants.MCQ_WEEK -> (168 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                MCQConstants.MCQ_MONTH -> (720 * MCQConstants.GRACE_DURATION_DISCOUNT).roundToInt()
                else -> 0
            }

            expiry.add(Calendar.HOUR, tempDuration)

            return ActivationExpiryDates(
                oldDate,
                dateFormat.format(expiry.time)
            )
        }

        /**
         * Generates a new expiry date.
         */
        fun generateNewExpiryDate(oldDate: String, duration: Long, timeType: String = SECONDS): String {
            val now = Calendar.getInstance()
            val expiry = Calendar.getInstance().apply {
                time = parseDate(oldDate)
            }

            val tempDuration = (duration / 1000).toInt()
            when (timeType) {
                SECONDS -> expiry.add(Calendar.SECOND, tempDuration)
            }

            if (now.time.after(expiry.time)) {
                now.add(Calendar.SECOND, tempDuration)
                expiry.time = now.time
            }

            return dateFormat.format(expiry.time)
        }
    }
}

