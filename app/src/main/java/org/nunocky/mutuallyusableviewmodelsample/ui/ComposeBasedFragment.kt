package org.nunocky.mutuallyusableviewmodelsample.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.nunocky.mutuallyusableviewmodelsample.CommonViewModel
import org.nunocky.mutuallyusableviewmodelsample.TaskXUiState

class ComposeBasedFragment : Fragment() {
    private val viewModel: CommonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
    val shouldShowDialog by viewModel.shouldShowDialog.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        Text("Count: $count")
        Button(onClick = { viewModel.increment() }) {
            Text("Increment")
        }

        when (val state = uiState) {
            TaskXUiState.Idle -> {
                Button(onClick = { viewModel.processASyncFunction() }) {
                    Text("Click")
                }
            }

            TaskXUiState.Loading -> {
                Text("loading...")
            }

            is TaskXUiState.Success -> {
                Column {
                    Text(state.text)
                    Button(onClick = { viewModel.processASyncFunction() }) {
                        Text("Click")
                    }
                }
            }

            is TaskXUiState.Error -> {
                Text(state.message)
                Button(onClick = { viewModel.processASyncFunction() }) {
                    Text("Click")
                }
            }
        }
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
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewComposesBasedScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        ComposesBasedScreen()
    }
}