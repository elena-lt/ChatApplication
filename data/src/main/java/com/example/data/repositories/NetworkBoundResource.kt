package com.example.data.repositories

import com.example.core.utils.DataState
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    //crossinline fun - forbids non-local returns in a lambda passed to an inline function
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {

    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(DataState.LOADING<ResultType>(true))

        try {
            saveFetchResult(fetch())
            query().map {
                DataState.SUCCESS<ResultType>(it)
            }
        } catch (throwable: Throwable) {
            query().map {
                DataState.ERROR<ResultType>(it.toString())
            }
        }
    } else {
        query().map {
            DataState.SUCCESS(it)
        }
    }
    emitAll(flow)

}
