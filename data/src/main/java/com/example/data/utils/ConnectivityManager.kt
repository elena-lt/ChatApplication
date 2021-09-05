package com.example.data.utils

import android.content.Context
import android.net.*
import android.net.ConnectivityManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@ExperimentalCoroutinesApi
class ConnectivityManager @Inject constructor(
    context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var isConnectedToInternet = false

    val networkStatus = callbackFlow{
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnectedToInternet = true
                offer(NetworkStatus.Available)
            }

            override fun onUnavailable() {
                isConnectedToInternet = false
                offer(NetworkStatus.Unavailable)
            }

            override fun onLost(network: Network) {
                isConnectedToInternet = false
                offer(NetworkStatus.Unavailable)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }

    inline fun <Result> Flow<NetworkStatus>.map(
        crossinline onAvailable: suspend () -> Result,
        crossinline onUnavailable: suspend () -> Result
    ): Flow<Result> = map {
        when (it) {
            NetworkStatus.Available -> onAvailable()
            NetworkStatus.Unavailable -> onUnavailable()
        }
    }

    sealed class NetworkStatus {
        object Available : NetworkStatus()
        object Unavailable : NetworkStatus()
    }

//    fun isConnectedToInternet(): Boolean {
//        var result = false
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val networkCapabilities = connectivityManager.activeNetwork ?: return false
//            val actNw =
//                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
//            result = when {
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                else -> false
//            }
//        } else {
//            connectivityManager.run {
//                connectivityManager.activeNetworkInfo?.run {
//                    result = when (type) {
//                        ConnectivityManager.TYPE_WIFI -> true
//                        ConnectivityManager.TYPE_MOBILE -> true
//                        ConnectivityManager.TYPE_ETHERNET -> true
//                        else -> false
//                    }
//
//                }
//            }
//        }
//
//        return result
//    }

}