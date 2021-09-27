package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.application.bmiobesity.model.retrofit.ResultCommonRecommendation

class HomeRecomCommonAdapterRecycler(
    private val onClick: (ResultCommonRecommendation) -> Unit
) : ListAdapter<ResultCommonRecommendation, HomeRecomCommonAdapterRecycler.HomeRecomCommonViewHolder>(
    HomeRecomDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecomCommonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.main_home_recommendation_card_view_common,
            parent,
            false
        )
        return HomeRecomCommonViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeRecomCommonViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class HomeRecomCommonViewHolder(
        itemView: View,
        val onClick: (ResultCommonRecommendation) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private var currentRecom: ResultCommonRecommendation? = null
        private val shortMsgTextView: TextView = itemView.findViewById(R.id.recommendationCardViewCommonMsgShort)
        private val longMsgTextView: TextView = itemView.findViewById(R.id.recommendationCardViewCommonMsgLong)

        init {
            itemView.setOnClickListener {
                currentRecom?.let {
                    onClick(it)
                }
            }
        }

        fun bind(recom: ResultCommonRecommendation) {
            currentRecom = recom
            val msgShort = recom.message_short
            val msgLong = recom.message_long

            if (!msgShort.isNullOrEmpty()) {
                shortMsgTextView.text = msgShort
            }
            if (!msgLong.isNullOrEmpty()) {
                longMsgTextView.text = msgLong
            }
        }
    }

    object HomeRecomDiffCallback : DiffUtil.ItemCallback<ResultCommonRecommendation>() {

        override fun areItemsTheSame(
            oldItem: ResultCommonRecommendation,
            newItem: ResultCommonRecommendation
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ResultCommonRecommendation,
            newItem: ResultCommonRecommendation
        ): Boolean {
            return (oldItem.message_short == newItem.message_short && oldItem.message_long == newItem.message_long)
        }

    }
}