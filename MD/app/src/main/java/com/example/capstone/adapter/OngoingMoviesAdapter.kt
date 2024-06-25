package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.ItemOngoingMovieBinding
import com.example.capstone.model.remote.Movie

class OngoingMoviesAdapter(private val onClick: (Movie) -> Unit) :
    androidx.paging.PagingDataAdapter<Movie, OngoingMoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemOngoingMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(movie, onClick)
        }
    }

    class MovieViewHolder(private val binding: ItemOngoingMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie, onClick: (Movie) -> Unit) {
            binding.movie = movie
            binding.root.setOnClickListener {
                onClick(movie)
            }
            binding.executePendingBindings()
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.movieid == newItem.movieid
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}