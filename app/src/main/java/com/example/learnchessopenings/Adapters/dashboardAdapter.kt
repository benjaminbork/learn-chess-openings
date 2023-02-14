package com.example.learnchessopenings.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
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
        holder.descriptionText.text = ItemsViewModel.description
        holder.progressCount.text = "${ItemsViewModel.progress}/${ItemsViewModel.totalProgress}"
        holder.progressBar.progress = ItemsViewModel.progress
        holder.progressBar.max = ItemsViewModel.totalProgress
        holder.img.setImageResource(ItemsViewModel.imageId)

        holder.reviewBtn.setOnClickListener {
            // This is where review button code goes
        }
        holder.learnBtn.setOnClickListener {
            // This is where learn button code goes
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val headerText: TextView = itemView.findViewById(R.id.courseTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.description)
        val progressCount: TextView = itemView.findViewById(R.id.progress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val img: ImageView = itemView.findViewById(R.id.imageView)
        val reviewBtn : Button = itemView.findViewById(R.id.review)
        val learnBtn : Button = itemView.findViewById(R.id.learn)
    }
}