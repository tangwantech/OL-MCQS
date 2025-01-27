package com.example.gceolmcqs

import android.os.CountDownTimer

class NetworkTimeout {

    companion object{
        private lateinit var timer: CountDownTimer
        fun checkTimeout(timeOutDuration: Long, onNetWorkTimeoutListener: OnNetWorkTimeoutListener){
            timer = object : CountDownTimer(timeOutDuration, 1000){
                override fun onTick(p0: Long) {
                }

                override fun onFinish() {
                    onNetWorkTimeoutListener.onNetworkTimeout()
                }

            }
            timer.start()
        }

        fun stopTimer(){
            timer.cancel()
        }
    }
    interface OnNetWorkTimeoutListener{
        fun onNetworkTimeout()
    }
}