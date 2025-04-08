package com.example.gceolmcqs

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class CheckInternetConnectivity {
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
    fun hasRealInternetAccess(): Boolean {
        return try {
            val timeout = 1500
            val url = URL("https://clients3.google.com/generate_204")
            val urlc = url.openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Android")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = timeout
            urlc.readTimeout = timeout
            urlc.connect()

            urlc.responseCode == 204 && urlc.contentLength == 0
        } catch (e: Exception) {
            false
        }
    }
}