package com.example.learnchessopenings.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.R
import com.example.learnchessopenings.ViewModels.detailedCourseViewModel

class detailedCourseAdapter(val mList: List<detailedCourseViewModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<detailedCourseAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_detail_card_element, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.headerText.text = ItemsViewModel.title
        when(ItemsViewModel.learned) {
            true -> holder.status.setImageResource(R.drawable.baseline_check_24)
            false -> holder.status.setImageResource(R.drawable.baseline_circle_24)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var headerText: TextView
        var status: ImageView
        init {
            headerText = itemView.findViewById(R.id.variationTitle)
            status = itemView.findViewById(R.id.learnStatusImg)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mList[position].id)
                }
            }
        }
    }
}