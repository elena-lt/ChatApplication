package com.example.data.utils

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

//inline fun <ResultType, RequestType> networkBoundResource(
//    crossinline query: () -> Flow<ResultType>,
//    crossinline fetch: suspend () -> RequestType,
//    crossinline saveFetchResult: suspend (RequestType) -> Unit,
//    crossinline shouldFetch: (ResultType) -> Boolean = { true }
//) = flow {
//    val data = query().first()
//
//    val flow = if (shouldFetch(data)) {
//        emit(Resource.Loading(data))
//
//        try {
//            saveFetchResult(fetch())
//            query().map { Resource.Success(it) }
//        } catch (throwable: Throwable) {
//            query().map { Resource.Error(throwable, it) }
//        }
//    } else {
//        query().map { Resource.Success(it) }
//    }
//
//    emitAll(flow)
//}
//
//fun getRestaurants() = networkBoundResource(
//    query = {
//        Log.d ("CashedData", "QUERY")
//        restaurantDao.getAllRestaurants()
//    },
//    fetch = {
//        delay(2000)
//        Log.d ("CashedData", "FETCH")
//        api.getRestaurants()
//    },
//    saveFetchResult = { restaurants ->
//
//        Log.d ("CashedData", "SAVE FETCH Result")
//        db.withTransaction {
//            restaurantDao.deleteAllRestaurants()
//            restaurantDao.insertRestaurants(restaurants)
//        }
//    }
//)