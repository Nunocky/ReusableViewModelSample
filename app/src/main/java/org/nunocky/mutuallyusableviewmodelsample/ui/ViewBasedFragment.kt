package org.nunocky.mutuallyusableviewmodelsample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nunocky.mutuallyusableviewmodelsample.CommonViewModel
import org.nunocky.mutuallyusableviewmodelsample.TaskXUiState
import org.nunocky.mutuallyusableviewmodelsample.databinding.FragmentViewBasedBinding

class ViewBasedFragment : Fragment() {
    private val viewModel: CommonViewModel by viewModels()

    private var _binding: FragmentViewBasedBinding? = null
    private val binding get() = _binding!!

    private val buttonVisibility = MutableStateFlow(View.VISIBLE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewBasedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnIncrement.setOnClickListener {
            viewModel.increment()
        }

        binding.btnAsynctask.setOnClickListener {
            viewModel.processASyncFunction()
        }

        lifecycleScope.launch {
            buttonVisibility.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                binding.btnIncrement.visibility = it
            }
        }

        lifecycleScope.launch {
            viewModel.count.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { count ->
                    binding.text1.text = "$count"
                }
        }

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        is TaskXUiState.Idle -> {
                            binding.text1.text = "Count : ${viewModel.count.value}"
                            buttonVisibility.value = View.VISIBLE
                        }

                        is TaskXUiState.Loading -> {
                            binding.text1.text = "loading..."
                            buttonVisibility.value = View.INVISIBLE
                        }

                        is TaskXUiState.Success -> {
                            binding.text1.text = uiState.text
                            buttonVisibility.value = View.VISIBLE
                        }

                        is TaskXUiState.Error -> {
                            binding.text1.text = uiState.message
                            buttonVisibility.value = View.VISIBLE
                        }
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.shouldShowDialog.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { shouldShowDialog ->
                    if (shouldShowDialog) {
                        showAlertDialog()
                    }
                }
        }
    }

    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Something went wrong")
            .setPositiveButton("OK") { _, _ ->
                viewModel.onDialogDismissed()
            }
            .setOnCancelListener {
                viewModel.onDialogDismissed()
            }
            .create()

        dialog.show()
    }
}