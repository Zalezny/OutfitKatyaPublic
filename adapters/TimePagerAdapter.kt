package com.example.outfitapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.outfitapp.stopwatch.StoperFragment
import com.example.outfitapp.fragments.WorkTimeFragment

private const val NUM_PAGES = 2

class TimePagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> StoperFragment()
            1 -> WorkTimeFragment()
            else -> createFragment(position)
        }
    }

    override fun getItemCount(): Int {
        return NUM_PAGES
    }
}