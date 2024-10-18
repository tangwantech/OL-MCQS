package com.example.gceolmcqs.repository

import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.SubjectPackageActivator
import com.example.gceolmcqs.datamodels.AppData
import com.example.gceolmcqs.datamodels.SubjectPackageData
import com.example.gceolmcqs.datamodels.SubjectPackages
import com.google.gson.Gson
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback
import com.parse.SignUpCallback

class RemoteRepoManager(private val deviceId: String) {
    companion object{
        private var deviceId: String? = null
        fun setDeviceID(deviceId: String){
            this.deviceId = deviceId
        }
        fun verifyDeviceIdInAppDatabase(callback: OnVerifyDataExistsListener){
            if (ParseUser.getCurrentUser() != null){
//            app is currently installed, user has accepted terms of use, and device is registered
                callback.onDeviceDataExists()
            }else{
//            app has just been installed. Checks if app was previously installed on device
                signInUser(callback)
            }

        }

        private fun signInUser(callback: OnVerifyDataExistsListener){

            ParseUser.logInInBackground(deviceId, deviceId){user, e ->
                if(user == null){
//                Device id does not exist in database. Therefore app has never been installed on device
                    signUpDevice{
                        if (it == null){
//                        signup successful and device id now exists in database
                            callback.onDeviceDataExists()
                        }else{
//                        error occurred during signup
                            callback.onError(it)
                        }
                    }
                }
                if (e == null){
//                signIn was successful because device id exists in database
                    callback.onDeviceDataExists()
                }else{
                    callback.onError(e)
                }

            }

        }


        private fun signUpDevice(callback: SignUpCallback){
            val user = ParseUser()
            user.username = deviceId
            user.setPassword(deviceId)
//        user.put(MCQConstants.SUBJECTS_PACKAGES, subjectPackages)
            val temp = ArrayList<SubjectPackageData>()
            val activatedSubjectPackages = SubjectPackageActivator.activateTrialPackageForAllSubjectsAvailable(MCQConstants.SUBJECTS_AVAILABLE)
            temp.addAll(activatedSubjectPackages)
            val subjectPackages = SubjectPackages(temp)
            val jsonString = Gson().toJson(subjectPackages)
            user.put(MCQConstants.SUBJECTS_PACKAGES, jsonString)
            user.signUpInBackground { e ->
                callback.done(e)

            }
        }

        fun setUserAppData(saveCallback: SaveCallback){
            val parseClass = ParseQuery.getQuery<ParseObject>(MCQConstants.OL_MCQ_DATA)
            parseClass.getInBackground(MCQConstants.APP_DATA_OBJECT_KEY){parseObject, queryException ->
                if (queryException == null){
                    val appData = parseObject.getString(MCQConstants.APP_DATA)
                    ParseUser.getCurrentUser().put(MCQConstants.APP_DATA, appData!!)

                    ParseUser.getCurrentUser().saveInBackground {
                        saveCallback.done(it)
                    }
                }
                else{
                    println(queryException.localizedMessage)
                }
            }
        }


        fun getUserSubjectPackages(): SubjectPackages{
            val jsonString = ParseUser.getCurrentUser().getString(MCQConstants.SUBJECTS_PACKAGES)
            val subjectsPackages = Gson().fromJson<SubjectPackages>(jsonString, SubjectPackages::class.java)
            return subjectsPackages
        }

        //    update user subject packages in app remote server
        fun updateUserSubjectPackages(subjectPackageData: SubjectPackageData, onUpdatePackageListener: OnUpdatePackageListener){

            val subjectPackages = getUserSubjectPackages().apply {
                subjectPackageDataList[subjectPackageData.subjectIndex!!] = subjectPackageData
            }
            val jsonString = Gson().toJson(subjectPackages)
            ParseUser.getCurrentUser().put(MCQConstants.SUBJECTS_PACKAGES, jsonString)
            ParseUser.getCurrentUser().saveInBackground {
                if (it == null){
                    onUpdatePackageListener.onUpDateSuccessful(subjectPackageData.subjectIndex!!)
                }else{
                    onUpdatePackageListener.onError()
                }
            }
        }

        fun getSubjectPackageDataAtIndex(index: Int): SubjectPackageData{
            return getUserSubjectPackages().subjectPackageDataList[index]
        }

        fun getOLMCQData(): AppData{
            return ParseUser.getCurrentUser()?.get(MCQConstants.APP_DATA) as AppData
        }



    }

    interface OnUpdatePackageListener{
        fun onUpDateSuccessful(index: Int)
        fun onError()
    }

    interface OnVerifyDataExistsListener{
        fun onDeviceDataExists()
        fun onError(e: ParseException)
    }


}