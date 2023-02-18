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

class dashboardAdapter(val mList: List<dashboardViewModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<dashboardAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(id: Int, action: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_course_detail_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        var progress = 0
        for(variation in ItemsViewModel.variations) {
            if(variation["learned"] == 1) {
                progress += 1
            }
        }

        holder.headerText.text = ItemsViewModel.header
        holder.descriptionText.text = ItemsViewModel.description
        holder.progressCount.text = "${progress}/${ItemsViewModel.variations.size}"
        holder.progressBar.progress = progress
        holder.progressBar.max = ItemsViewModel.variations.size
        holder.img.setImageResource(ItemsViewModel.imageId)

        /* holder.reviewBtn.setOnClickListener {
            // This is where review button code goes
        }
        holder.learnBtn.setOnClickListener {
            // This is where learn button code goes
        }*/
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        var headerText: TextView
        var descriptionText: TextView
        var progressCount: TextView
        var progressBar: ProgressBar
        var img: ImageView
        var reviewBtn: Button
        var learnBtn: Button

        init {
            headerText = itemView.findViewById(R.id.courseTitle)
            descriptionText = itemView.findViewById(R.id.description)
            progressCount = itemView.findViewById(R.id.progress)
            progressBar = itemView.findViewById(R.id.progressBar)
            img = itemView.findViewById(R.id.imageView)
            reviewBtn = itemView.findViewById(R.id.review)
            learnBtn = itemView.findViewById(R.id.learn)

            reviewBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mList[position].id, "review")
                }
            }
            learnBtn.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mList[position].id, "learn")
                }
            }
        }
    }
}