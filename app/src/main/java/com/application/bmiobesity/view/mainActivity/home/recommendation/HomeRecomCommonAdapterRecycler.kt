package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.application.bmiobesity.model.retrofit.ResultCommonRecommendation

class HomeRecomCommonAdapterRecycler {

    class HomeRecomCommonViewHolder(itemView: View, val onClick: (ResultCommonRecommendation) -> Unit): RecyclerView.ViewHolder(itemView){
        private var currentRecom: ResultCommonRecommendation? = null
        private val message: TextView = itemView.findViewById(R.id.recommendationCardViewText)

        init {

        }
    }
}