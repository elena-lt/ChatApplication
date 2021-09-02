package com.example.data.repositories

import android.util.Log
import com.example.core.utils.DataState
import com.example.data.utils.ConnectivityManager
import com.example.data.utils.Const
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.xml.transform.Result

//inline fun <ResultType, RequestType> networkBoundResourceFun(
//    //crossinline fun - forbids non-local returns in a lambda passed to an inline function
//    forceFetch: Boolean,
//    crossinline query: () -> Flow<ResultType>,
//    crossinline fetch: suspend () -> RequestType,
//    crossinline saveFetchResult: suspend (RequestType) -> Unit,
//    crossinline shouldFetch: () -> Boolean
//) = flow {
//
//    val data = query().first()
//
//    val flow = if (shouldFetch()) {
//        emit(DataState.LOADING<ResultType>(true))
//
//        try {
//            saveFetchResult(fetch())
//            query().map {
//                DataState.SUCCESS<ResultType>(it)
//            }
//        } catch (throwable: Throwable) {
//            query().map {
//                DataState.ERROR<ResultType>(it.toString())
//            }
//        }
//    } else {
//        query().map {
//            DataState.SUCCESS<ResultType>(it)
//        }
//    }
//    emitAll(flow)
//}

@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<RequestType, ResultType>
constructor(
    private val connectivityManager: ConnectivityManager
) {

    val flow = flow<DataState<ResultType>> {
        emit(DataState.LOADING(true))
        if (forceFetch()) {
            if (isNetworkAvailable()) {
                saveFetchResult(createCall())
                emit(loadFromDB())
            } else emit(loadFromDB())
        } else {
            emit(loadFromDB())
            if (isNetworkAvailable()) {
                saveFetchResult(createCall())
                emit(loadFromDB())
            }
        }
    }

//    private suspend fun handleQueries()
//    = flow<DataState<ResultType>>
//    {
//        Log.d("ApDebug", "handleQueries: ")
//        if (isNetworkAvailable()) {
//            try {
//                saveFetchResult(createCall().data)
//                emit(loadFromDB())
//            } catch (throwable: Throwable) {
//                onFetchFailed(throwable.toString())
//                emit(loadFromDB())
//                emit(DataState.ERROR(throwable.message ?: Const.UNKNOWN_ERROR))
//            }
//        } else {
//            emit(loadFromDB())
//        }
//    }

    protected open fun forceFetch(): Boolean = false
    protected open fun isNetworkAvailable(): Boolean {
        return connectivityManager.isConnectedToInternet
    }

    protected abstract suspend fun loadFromDB(): DataState<ResultType>
    protected abstract suspend fun createCall(): RequestType?
    protected open fun onFetchFailed(throwable: String) {}
    protected open suspend fun saveFetchResult(data: RequestType?) {}
}