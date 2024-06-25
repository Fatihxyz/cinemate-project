package com.example.capstone.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.capstone.view.HomeFragment
import com.example.capstone.view.TheaterFragment
import com.example.capstone.view.AccountFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGES = 3

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> TheaterFragment()
            2 -> AccountFragment()
            else -> HomeFragment()
        }
    }
}
