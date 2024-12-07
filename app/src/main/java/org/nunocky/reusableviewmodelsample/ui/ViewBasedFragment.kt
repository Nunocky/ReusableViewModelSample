package org.nunocky.reusableviewmodelsample.ui

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
import kotlinx.coroutines.launch
import org.nunocky.reusableviewmodelsample.CommonViewModel
import org.nunocky.reusableviewmodelsample.R
import org.nunocky.reusableviewmodelsample.TaskXUiState
import org.nunocky.reusableviewmodelsample.databinding.FragmentViewBasedBinding
import org.nunocky.reusableviewmodelsample.util.watchFlow
import org.nunocky.reusableviewmodelsample.util.watchStateFlow

class ViewBasedFragment : Fragment() {
    private val viewModel: CommonViewModel by viewModels()

    private var _binding: FragmentViewBasedBinding? = null
    private val binding get() = _binding!!


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

//        lifecycleScope.launch {
//            viewModel.shouldShowButton.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collect {
//                    val visibility = if (it) View.VISIBLE else View.INVISIBLE
//                    binding.btnIncrement.visibility = visibility
//                    binding.btnAsynctask.visibility = visibility
//                }
//        }

        watchFlow(viewModel.shouldShowButton) {
            val visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.btnIncrement.visibility = visibility
            binding.btnAsynctask.visibility = visibility
        }

        lifecycleScope.launch {
            viewModel.count.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { count ->
                    binding.text1.text =
                        requireContext().getString(R.string.count, count)
                }
        }
//        watchStateFlow(viewModel.count) { count ->
//            binding.text1.text = requireContext().getString(R.string.count, count)
//        }

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        is TaskXUiState.Idle -> {
                            binding.text2.text = ""
                        }

                        is TaskXUiState.Loading -> {
                            binding.text2.text = requireContext().getString(R.string.loading)
                        }

                        is TaskXUiState.Success -> {
                            binding.text2.text = uiState.text
                        }

                        is TaskXUiState.Error -> {
                            binding.text2.text = uiState.message
                        }
                    }
                }
        }

//        watchStateFlow(viewModel.uiState) {
//            when (it) {
//                is TaskXUiState.Idle -> {
//                    binding.text2.text = ""
//                }
//
//                is TaskXUiState.Loading -> {
//                    binding.text2.text = requireContext().getString(R.string.loading)
//                }
//
//                is TaskXUiState.Success -> {
//                    binding.text2.text = it.text
//                }
//
//                is TaskXUiState.Error -> {
//                    binding.text2.text = it.message
//                }
//            }
//        }

//        lifecycleScope.launch {
//            viewModel.shouldShowDialog.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collect { shouldShowDialog ->
//                    if (shouldShowDialog) {
//                        showAlertDialog()
//                    }
//                }
//        }
        watchStateFlow(viewModel.shouldShowDialog) { shouldShowDialog ->
            if (shouldShowDialog) {
                showAlertDialog()
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