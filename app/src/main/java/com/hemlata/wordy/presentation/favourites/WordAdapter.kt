package com.hemlata.wordy.presentation.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hemlata.wordy.data.local.WordEntity
import com.hemlata.wordy.databinding.ItemWordBinding

class WordAdapter(
    private val onRemove: (WordEntity) -> Unit
) : ListAdapter<WordEntity, WordAdapter.WordViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WordViewHolder(
        private val binding: ItemWordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: WordEntity) {
            binding.tvAvatar.text = word.word.first().uppercaseChar().toString()
            binding.tvWord.text = word.word
            binding.tvPhonetic.text = word.phonetic ?: ""
            binding.tvDefinition.text = word.definitions
                .split("|").firstOrNull() ?: ""
            binding.btnRemove.setOnClickListener { onRemove(word) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WordEntity>() {
        override fun areItemsTheSame(oldItem: WordEntity, newItem: WordEntity) =
            oldItem.word == newItem.word
        override fun areContentsTheSame(oldItem: WordEntity, newItem: WordEntity) =
            oldItem == newItem
    }
}