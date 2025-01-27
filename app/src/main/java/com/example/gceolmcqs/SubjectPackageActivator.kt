package com.example.gceolmcqs

import com.example.gceolmcqs.datamodels.SubjectPackageData

class SubjectPackageActivator {
    companion object{

        fun activateTrialPackageForAllSubjectsAvailable(availableSubjects: List<String>?): List<SubjectPackageData>{
            val activationExpiryDates =
                ActivationExpiryDatesGenerator.generateActivationExpiryDates(
                    MCQConstants.HOURS,
                    MCQConstants.TRIAL_DURATION
                )

            val packageDataList = ArrayList<SubjectPackageData>()
            availableSubjects?.forEachIndexed { subjectIndex, subject ->

                packageDataList.add(
                    SubjectPackageData(
                        subjectIndex,
                        subject,
                        "TRIAL",
                        activationExpiryDates.activatedOn,
                        activationExpiryDates.expiresOn,
                        isPackageActive = true
                    )
                )

            }
            return packageDataList
        }

        fun activateSubjectPackage(tempSubjectName: String, tempSubjectIndex: Int, packageType: String, packageDuration: Int): SubjectPackageData {
            val activationExpiryDates =
                ActivationExpiryDatesGenerator.generateActivationExpiryDates(
                    MCQConstants.HOURS,
                    packageDuration
                )

//            println(activationExpiryDates)

            val subjectPackageData = SubjectPackageData().apply {
                subjectIndex = tempSubjectIndex
                subjectName = tempSubjectName
                packageName = packageType
                activatedOn = activationExpiryDates.activatedOn
                expiresOn = activationExpiryDates.expiresOn
                isPackageActive = true
            }

            return subjectPackageData
        }

    }


}