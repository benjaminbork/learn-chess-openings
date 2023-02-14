package com.example.learnchessopenings.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.DetailedCourse
import com.example.learnchessopenings.R
import com.example.learnchessopenings.ViewModels.courseViewModel

class courseAdapter(val mList: List<courseViewModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<courseAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_overview_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.headerText.text = ItemsViewModel.title
        holder.img.setImageResource(ItemsViewModel.img)

        /* holder.card.setOnClickListener {
            // This is where card click code goes
        } */
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var headerText: TextView
        var img: ImageView
        var card: CardView
        init {
            headerText = itemView.findViewById(R.id.MiniCourseTitle)
            img = itemView.findViewById(R.id.MiniCourseImage)
            card = itemView.findViewById(R.id.courseCard)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mList[position].id)
                }
            }
        }
    }
}