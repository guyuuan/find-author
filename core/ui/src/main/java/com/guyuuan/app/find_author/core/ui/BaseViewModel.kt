package com.guyuuan.app.find_author.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: Chen
 * @createTime: 6/11/24 17:08
 * @description:
 **/
abstract class BaseViewModel<State : UiState, Event : UiEvent> : ViewModel() {
    abstract val uiState: StateFlow<State>

    protected abstract suspend fun onEvent(event: Event)
    private val eventChannel = Channel<Unit>(1)
    fun dispatch(event: Event) = viewModelScope.launch(event.dispatcher) {
        eventChannel.runBlock {
            try {
                onEvent(event)
                withContext(Dispatchers.Main) { event.onSuccess?.invoke() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { event.onFailed?.invoke(e) }
            }
        }
    }
}

context(CoroutineScope)
fun Channel<Unit>.runBlock(block: suspend () -> Unit) {
    launch {
        send(Unit)
        launch {
            block()
        }.apply {
            invokeOnCompletion {
                launch { tryReceive() }
            }
        }
    }
}

typealias UiEventOnSuccess = () -> Unit
typealias UiEventOnFailed = (Throwable) -> Unit

interface UiEvent {
    val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val onSuccess: UiEventOnSuccess?
        get() = null
    val onFailed: UiEventOnFailed?
        get() = null
}

interface UiState