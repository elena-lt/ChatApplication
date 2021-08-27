package com.example.chatapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseViewModel <ViewState, StateEvent, DataState>: ViewModel() {

    protected val _stateEvent: MutableSharedFlow<StateEvent> = MutableSharedFlow()
    val stateEvent get() = _stateEvent.asSharedFlow()

    private val initViewState: ViewState by lazy {createInitialState()}
    abstract fun createInitialState(): ViewState

    protected val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initViewState)
    val viewState get() = _viewState.asSharedFlow()

    private val _dataState: MutableSharedFlow<DataState> = MutableSharedFlow()
    val dataState = _dataState.asSharedFlow()

    val currentState: ViewState
    get() = _viewState.value

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            stateEvent.collect {
                handleStateEvent(it)
            }
        }
    }

    abstract fun handleStateEvent(stateEvent: StateEvent)

    fun setStateEvent(stateEvent: StateEvent){
        viewModelScope.launch { _stateEvent.emit(stateEvent) }
    }

    fun setViewState(viewState: ViewState){
        _viewState.value = viewState
    }

    fun setDataState(dataState: DataState){
        viewModelScope.launch {
            _dataState.emit(dataState)
        }
    }
}