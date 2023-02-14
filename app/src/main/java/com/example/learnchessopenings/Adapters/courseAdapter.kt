package com.example.learnchessopenings.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.R
import com.example.learnchessopenings.ViewModels.courseViewModel

class courseAdapter(val mList: List<courseViewModel>) : RecyclerView.Adapter<courseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_overview_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.headerText.text = ItemsViewModel.title
        holder.img.setImageResource(ItemsViewModel.img)

        holder.card.setOnClickListener {
            // This is where card click code goes

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val headerText: TextView = itemView.findViewById(R.id.MiniCourseTitle)
        val img: ImageView = itemView.findViewById(R.id.MiniCourseImage)
        val card: CardView = itemView.findViewById(R.id.courseCard)
    }
}