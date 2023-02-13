package com.example.learnchessopenings.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.R
import com.example.learnchessopenings.ViewModels.dashboardViewModel

class dashboardAdapter(val mList: List<dashboardViewModel>) : RecyclerView.Adapter<dashboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_course_detail_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.headerText.text = ItemsViewModel.header

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val headerText: TextView = itemView.findViewById(R.id.courseTitle)
    }
}