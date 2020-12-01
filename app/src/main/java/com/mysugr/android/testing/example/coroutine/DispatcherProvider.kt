package com.mysugr.android.testing.example.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val default = Dispatchers.Default
    override val io = Dispatchers.Default
    override val main = Dispatchers.Default
    override val unconfined = Dispatchers.Default
}
