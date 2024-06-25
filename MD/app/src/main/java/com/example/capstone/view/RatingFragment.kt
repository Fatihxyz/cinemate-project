package com.example.capstone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.capstone.R

class RatingFragment : DialogFragment() {

    interface RatingDialogListener {
        fun onSubmitRating(rating: Float)
    }

    private lateinit var ratingText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: Button
    private var listener: RatingDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rating, container, false)

        ratingText = view.findViewById(R.id.rating_text)
        ratingBar = view.findViewById(R.id.rating_bar)
        submitButton = view.findViewById(R.id.submit_review_button)

        submitButton.setOnClickListener {
            listener?.onSubmitRating(ratingBar.rating)
            ratingText.text = "Thank you for submitting your rating"
            view.postDelayed({ dismiss() }, 2000)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun setRatingDialogListener(listener: RatingDialogListener) {
        this.listener = listener
    }
}
