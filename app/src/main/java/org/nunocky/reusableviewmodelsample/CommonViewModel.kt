package org.nunocky.reusableviewmodelsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

suspend fun taskXFunction(count: Int): String {
    delay(1500)

    if (count % 3 == 0) {
        throw Exception("error")
    }

    return "Hello"
}

sealed class TaskXUiState {
    data object Idle : TaskXUiState()
    data object Loading : TaskXUiState()
    data class Success(val text: String) : TaskXUiState()
    data class Error(val message: String) : TaskXUiState()
}

class CommonViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()

    private val _uiState = MutableStateFlow<TaskXUiState>(TaskXUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val shouldShowButton = _uiState.map { it !is TaskXUiState.Loading }

    private var _shouldShowDialog = MutableStateFlow(false)
    val shouldShowDialog = _shouldShowDialog.asStateFlow()

    fun increment() {
        _count.value++
    }

    fun processASyncFunction() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                _uiState.value = TaskXUiState.Loading
                taskXFunction(count.value)
            }.onSuccess { text ->
                _uiState.value = TaskXUiState.Success(text)
            }.onFailure {
                _shouldShowDialog.value = true
                _uiState.value = TaskXUiState.Error(it.message ?: "unknown error")
            }
        }
    }

    fun onDialogDismissed() {
        _shouldShowDialog.value = false
    }
}
