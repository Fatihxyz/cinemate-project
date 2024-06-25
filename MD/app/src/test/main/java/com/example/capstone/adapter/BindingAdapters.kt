package com.example.capstone.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("timestampToFormattedDate")
fun bindTimestampToFormattedDate(view: TextView, timestamp: Long) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = sdf.format(Date(timestamp))
    view.text = formattedDate
}
