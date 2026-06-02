package com.hemlata.wordy.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hemlata.wordy.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { word ->
            val action = HistoryFragmentDirections
                .actionHistoryFragmentToSearchFragment(word)
            findNavController().navigate(action)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.history.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)

            val count = history.size
            binding.tvHistoryCount.text = if (count == 0) "no searches yet"
            else "$count recent ${if (count == 1) "search" else "searches"}"

            if (history.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
                binding.btnClearHistory.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                binding.btnClearHistory.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}