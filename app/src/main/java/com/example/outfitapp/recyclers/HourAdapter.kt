package com.example.outfitapp.recyclers


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.outfitapp.R

import com.example.outfitapp.models.TimeDataModel


class HourAdapter(private val hourList : ArrayList<TimeDataModel>) : RecyclerView.Adapter<HourAdapter.HourViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.hour_item,
            parent, false
        )
        return HourViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {

//        val currentItem = hourList[hourList.size - 1 - position]
        val currentItem = hourList[position]


        holder.tvDate.text = currentItem.date

        holder.tvTime.text = currentItem.time

    }

    override fun getItemCount(): Int {
        return hourList.size
    }


    class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvDate: TextView = itemView.findViewById(R.id.tv_date_hours_item)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time_hours_item)

    }

    fun addItem(item: TimeDataModel) {
        hourList.add(0, item)
        notifyDataSetChanged()
    }
}