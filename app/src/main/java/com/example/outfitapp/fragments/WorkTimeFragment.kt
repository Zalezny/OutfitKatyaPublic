package com.example.outfitapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.outfitapp.R
import com.example.outfitapp.adapters.WorkTimePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Parent Fragment for KatyaFragment and MumFragment **/

class WorkTimeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_work_time, container, false)

        val tabLayout : TabLayout = v.findViewById(R.id.work_tab_layout)
        val viewPager2 : ViewPager2 = v.findViewById(R.id.work_vp2)

        val adapter = WorkTimePagerAdapter(activity!!)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
            tab.text = if (pos == 0) "MAMA" else "KASIA"
        }.attach()


        return v
    }



}