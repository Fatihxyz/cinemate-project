package com.example.capstone.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.model.remote.TicketRequest
import java.text.SimpleDateFormat

class TicketAdapter(private val tickets: List<TicketRequest>) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int = tickets.size

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ticketIdTextView: TextView = itemView.findViewById(R.id.ticketIdTextView)
        private val movieIdTextView: TextView = itemView.findViewById(R.id.movieIdTextView)
        private val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        private val showTimeTextView: TextView = itemView.findViewById(R.id.showTimeTextView)
        private val purchaseTimeTextView: TextView = itemView.findViewById(R.id.purchaseTimeTextView)

        @SuppressLint("SimpleDateFormat")
        private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        @SuppressLint("SimpleDateFormat")
        private val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        fun bind(ticket: TicketRequest) {
            ticketIdTextView.text = "Ticket ID: ${ticket.ticketid}"
            movieIdTextView.text = "Movie ID: ${ticket.movieid}"
            movieTitleTextView.text = "Movie Title: ${ticket.movie_title}"
            showTimeTextView.text = "Show Time: ${formatDate(ticket.show_time)}"
            purchaseTimeTextView.text = "Purchase Time: ${formatDate(ticket.purchase_time)}"
        }

        private fun formatDate(dateString: String): String {
            return try {
                val date = inputFormat.parse(dateString)
                outputFormat.format(date)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
