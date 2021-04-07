package com.application.bmiobesity.view.mainActivity.home.favorite

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.utils.getFormatData

class HomeFavoriteAdapterRecycler(private val onClick: (ResultCard) -> Unit) : ListAdapter<ResultCard, HomeFavoriteAdapterRecycler.HomeFavoriteViewHolder>(ResultCardDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_home_favorite_card_view, parent, false)
        return HomeFavoriteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeFavoriteViewHolder, position: Int) {
        val favoriteCard = getItem(position)
        holder.bind(favoriteCard)
    }

    class HomeFavoriteViewHolder(itemView: View, val onClick: (ResultCard) -> Unit) : RecyclerView.ViewHolder(itemView){
        private val favoriteCardImage: ImageView = itemView.findViewById(R.id.favoriteCardViewIcon)
        private val favoriteCardTitle: TextView = itemView.findViewById(R.id.favoriteCardViewTitle)
        private val favoriteCardValue: TextView = itemView.findViewById(R.id.favoriteCardViewValue)
        private val favoriteCardDate: TextView = itemView.findViewById(R.id.favoriteCardViewData)
        private var currentCard: ResultCard? = null

        init {
            (itemView.findViewById(R.id.favoriteCardViewMenu) as ImageView).setOnClickListener  {
                currentCard?.let {
                    onClick(it)
                }
            }
        }

        fun bind(card: ResultCard){
            currentCard = card
            val idImage = InTimeApp.APPLICATION.resources.getIdentifier(card.imgRes, "drawable", "com.application.bmiobesity")
            val idTitle = InTimeApp.APPLICATION.resources.getIdentifier(card.nameRes, "string", "com.application.bmiobesity")

            favoriteCardImage.setImageResource(idImage)
            favoriteCardTitle.setText(idTitle)
            favoriteCardDate.text = getFormatData("dd.MM.yyyy")

            favoriteCardValue.text = card.value
            if(card.valueColour.isNotEmpty()) favoriteCardValue.setTextColor(Color.parseColor(card.valueColour))
        }
    }

    object ResultCardDiffCallback : DiffUtil.ItemCallback<ResultCard>(){
        override fun areItemsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
            return (oldItem.id == newItem.id && oldItem.value == newItem.value && oldItem.valueColour == newItem.valueColour)
        }
    }
}