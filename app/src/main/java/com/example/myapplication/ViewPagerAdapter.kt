package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.EventViewHolder>() {

    val eventlist = listOf("Axes", "Tool(E)", "General")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder{
        return EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.test_cell, parent, false))
    }

    override fun getItemCount() = eventlist.count()

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        (holder.view as? TextView)?.also {
            it.text = eventlist.get(position)

        }
    }

    class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}