package com.example.outfitapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.outfitapp.fragments.KatyaFragment
import com.example.outfitapp.fragments.MumFragment

private const val NUM_PAGES = 2

class WorkTimePagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MumFragment()
            1 -> KatyaFragment()
            else -> createFragment(position)
        }
    }
}