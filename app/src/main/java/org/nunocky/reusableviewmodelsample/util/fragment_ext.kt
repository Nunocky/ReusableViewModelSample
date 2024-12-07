package org.nunocky.reusableviewmodelsample.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> Fragment.watchStateFlow(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    lifecycleScope.launch {
        stateFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect() { v ->
            block.invoke(v)
        }
    }
}
