package org.nunocky.reusableviewmodelsample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.nunocky.reusableviewmodelsample.CommonViewModel
import org.nunocky.reusableviewmodelsample.R
import org.nunocky.reusableviewmodelsample.TaskXUiState


class ComposeBasedFragment : Fragment() {
    private val viewModel: CommonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposesBasedScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposesBasedScreen(viewModel: CommonViewModel = viewModel(), modifier: Modifier = Modifier) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shouldShowButton by viewModel.shouldShowButton.collectAsStateWithLifecycle(
        initialValue = true,
        lifecycleOwner = LocalLifecycleOwner.current
    )
    val shouldShowDialog by viewModel.shouldShowDialog.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        CounterSection(count, shouldShowButton, onClick = { viewModel.increment() })
        TaskSection(uiState, shouldShowButton, onClick = { viewModel.processASyncFunction() })
    }

    if (shouldShowDialog) {
        BasicAlertDialog(onDismissRequest = {
            viewModel.onDialogDismissed()
        }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "something went wrong")
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            viewModel.onDialogDismissed()
                        }, modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun CounterSection(
    count: Int = 0,
    shouldShowButton: Boolean = true,
    onClick: () -> Unit = { },
) {
    val context = LocalContext.current

    Column {
        Text(context.getString(R.string.count, count))
        if (shouldShowButton) {
            Button(onClick = onClick) {
                Text("Increment")
            }
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun TaskSection(
    uiState: TaskXUiState = TaskXUiState.Idle,
    shouldShowButton: Boolean = true,
    onClick: () -> Unit = { },
) {
    val context = LocalContext.current

    when (uiState) {
        TaskXUiState.Idle -> {
            Text("")
        }

        TaskXUiState.Loading -> {
            Text(context.getString(R.string.loading))
        }

        is TaskXUiState.Success -> {
            Column {
                Text(uiState.text)
            }
        }

        is TaskXUiState.Error -> {
            Text(uiState.message)
        }
    }

    if (shouldShowButton) {
        Button(onClick = onClick) {
            Text("Click")
        }
    } else {
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview
@Composable
fun PreviewComposesBasedScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        ComposesBasedScreen()
    }
}