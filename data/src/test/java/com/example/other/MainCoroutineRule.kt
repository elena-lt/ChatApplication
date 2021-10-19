package com.example.other

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//@ExperimentalCoroutinesApi
//class MainCoroutineRule(
//    private val dispatcher: CoroutineDispatcher = TestC()
//) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher){
//
//    override fun starting(description: Description?) {
//        super.starting(description)
//        Dispatchers.setMain(dispatcher)
//    }
//
//    override fun finished(description: Description?) {
//        super.finished(description)
//        cleanupTestCoroutines()
//        Dispatchers.resetMain()
//    }
//}