package com.example.gceolmcqs

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ConnectivityTester {
    companion object{
        fun checkConnection(context: Context, params: HashMap<String, String>, listener: OnTestConnectionListener){
            val client: OkHttpClient = OkHttpClient.Builder().build()
            val url = "https://parseapi.back4app.com/functions/testConnection"


            val mediaType = "application/json;charset=utf-8".toMediaType()
            val requestBody = RequestBody.create(mediaType, JSONObject(params as Map<*, *>).toString())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Parse-Application-Id", context.getString(R.string.back4app_app_id))
                .addHeader("X-Parse-REST-API-Key", context.getString(R.string.back4app_client_key))
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    call.cancel()
                    listener.onConnectionUnavailable()
                }

                override fun onResponse(call: Call, response: Response) {
                    listener.onConnectionAvailable()

                }
            })

        }
    }



    interface OnTestConnectionListener{
        fun onConnectionAvailable()
        fun onConnectionUnavailable()

    }
}