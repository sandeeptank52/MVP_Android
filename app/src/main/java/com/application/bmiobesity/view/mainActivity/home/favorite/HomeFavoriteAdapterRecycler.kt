package com.application.bmiobesity.view.mainActivity.home.favorite

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard

class HomeFavoriteAdapterRecycler(private val onClick: (ResultCard) -> Unit) {

    class HomeFavoriteViewHolder(itemView: View, val onClick: (ResultCard) -> Unit) : RecyclerView.ViewHolder(itemView){
        private var currentCard: ResultCard? = null

        init {
            itemView.setOnClickListener {
                currentCard?.let {
                    onClick(it)
                }
            }
        }
    }

    object ResultCardDiffCallback : DiffUtil.ItemCallback<ResultCard>(){
        override fun areItemsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
            return oldItem.id == newItem.id
        }

    }
}