package com.example.taskprioritylist.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension equivalent to a JUnit 4 TestRule.
 * Replaces Dispatchers.Main with a test dispatcher for each test,
 * enabling ViewModel unit tests without an Android runtime.
 */
class MainDispatcherExtension : BeforeEachCallback, AfterEachCallback {
    val testDispatcher = UnconfinedTestDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
