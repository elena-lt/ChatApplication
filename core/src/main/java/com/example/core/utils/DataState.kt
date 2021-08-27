package com.example.core.utils

sealed class DataState<T>(
    var loading: Boolean,
    var data: T? = null,
    var errorMessage: String? = null
) {
    class LOADING<T>(
        isLoading: Boolean,
    ) : DataState<T>(
        loading = isLoading,
    )

    class SUCCESS<T>(
        data: T? = null,
        errorMessage: String? = null
    ) : DataState<T>(
        loading = false,
        data = data,
        errorMessage = errorMessage
    )

    class ERROR<T>(
        errorMessage: String
    ) : DataState<T>(
        loading = false,
        data = null,
        errorMessage = errorMessage
    )
}