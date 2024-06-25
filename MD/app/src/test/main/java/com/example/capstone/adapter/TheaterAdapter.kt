package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.ItemTheaterBinding
import com.example.capstone.model.remote.Theater

class TheaterAdapter(private val onClick: (Theater) -> Unit) :
    androidx.paging.PagingDataAdapter<Theater, TheaterAdapter.TheaterViewHolder>(TheaterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheaterViewHolder {
        val binding = ItemTheaterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TheaterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TheaterViewHolder, position: Int) {
        val theater = getItem(position)
        if (theater != null) {
            holder.bind(theater, onClick)
        }
    }

    class TheaterViewHolder(private val binding: ItemTheaterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(theater: Theater, onClick: (Theater) -> Unit) {
            binding.theater = theater
            binding.root.setOnClickListener {
                onClick(theater)
            }
            binding.executePendingBindings()
        }
    }

    class TheaterDiffCallback : DiffUtil.ItemCallback<Theater>() {
        override fun areItemsTheSame(oldItem: Theater, newItem: Theater): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Theater, newItem: Theater): Boolean {
            return oldItem == newItem
        }
    }
}
