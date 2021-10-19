package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.application.bmiobesity.model.retrofit.ResultRecommendation

class HomeRecomPersonalAdapterRecycler(private val onClick: (ResultRecommendation) -> Unit): ListAdapter<ResultRecommendation, HomeRecomPersonalAdapterRecycler.HomeRecomPersonalViewHolder>(HomeRecomPersonalDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecomPersonalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_home_recommendation_card_view_personal, parent, false)
        return HomeRecomPersonalViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeRecomPersonalViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class HomeRecomPersonalViewHolder(itemView: View, val onClick: (ResultRecommendation) -> Unit): RecyclerView.ViewHolder(itemView){
        private var currentRecom: ResultRecommendation? = null
        private val message: TextView = itemView.findViewById(R.id.recommendationCardViewText)

        init {
            itemView.setOnClickListener {
                currentRecom?.let {
                    onClick(it)
                }
            }
        }

        fun bind(recom: ResultRecommendation){
            currentRecom = recom
            val msg = recom.name
            if (!msg.isNullOrEmpty()) message.text = msg
        }
    }

    object HomeRecomPersonalDiffCallback: DiffUtil.ItemCallback<ResultRecommendation>(){
        override fun areItemsTheSame(oldItem: ResultRecommendation, newItem: ResultRecommendation): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ResultRecommendation, newItem: ResultRecommendation): Boolean {
            return oldItem.name == newItem.name
        }

    }
}