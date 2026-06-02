package com.hemlata.wordy.presentation.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hemlata.wordy.databinding.FragmentFavouritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouritesViewModel by viewModels()
    private lateinit var adapter: WordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = WordAdapter { word ->
            viewModel.removeFavourite(word)
        }
        binding.rvFavourites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavourites.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.favourites.observe(viewLifecycleOwner) { words ->
            adapter.submitList(words)

            val count = words.size
            binding.tvSavedCount.text = if (count == 0) "no words saved"
            else "$count ${if (count == 1) "word saved" else "words saved"}"

            if (words.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvFavourites.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvFavourites.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}