package com.hemlata.wordy.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hemlata.wordy.data.local.HistoryEntity
import com.hemlata.wordy.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(history: HistoryEntity) {
            binding.tvWord.text = history.word
            val time = android.text.format.DateUtils.getRelativeTimeSpanString(
                history.searchedAt,
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS
            )
            binding.tvTime.text = time
            binding.root.setOnClickListener { onClick(history.word) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity) =
            oldItem.word == newItem.word
        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity) =
            oldItem == newItem
    }
}