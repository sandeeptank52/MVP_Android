package com.application.bmiobesity.view.mainActivity.home.favorite

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.utils.getFormatData
import kotlinx.android.synthetic.main.main_home_favorite_card_view.view.*

class HomeFavoriteAdapterRecycler(
    private val onClick: (ResultCard) -> Unit
) : ListAdapter<ResultCard, HomeFavoriteAdapterRecycler.HomeFavoriteViewHolder>(
    ResultCardDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.main_home_favorite_card_view,
            parent,
            false
        )
        return HomeFavoriteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeFavoriteViewHolder, position: Int) {
        val favoriteCard = getItem(position)
        holder.bind(favoriteCard)
    }

    class HomeFavoriteViewHolder(
        itemView: View, val onClick: (ResultCard) -> Unit
    ) : RecyclerView.ViewHolder(
        itemView
    ) {

        private val favoriteCardImage: ImageView = itemView.findViewById(R.id.favoriteCardViewIcon)
        private val favoriteCardTitle: TextView = itemView.findViewById(R.id.favoriteCardViewTitle)
        private val favoriteCardValue: TextView = itemView.findViewById(R.id.favoriteCardViewValue)
        private val favoriteCardDate: TextView = itemView.findViewById(R.id.favoriteCardViewData)
        private val favoriteCardDetail : TextView = itemView.findViewById(R.id.favoriteCardViewDetails)
        private val favoriteCardDetailView :ConstraintLayout = itemView.findViewById(R.id.favoriteCardViewDetailView)
        private var currentCard: ResultCard? = null

        init {
//            itemView.setOnClickListener {
//                currentCard?.let {
//                    onClick(it)
//                }
//            }
        }

        fun bind(card: ResultCard) {
            currentCard = card

            // Resource ID
            val idImage = InTimeApp.APPLICATION.resources.getIdentifier(
                card.imgRes,
                "drawable",
                "com.application.bmiobesity"
            )
            val idTitle = InTimeApp.APPLICATION.resources.getIdentifier(
                card.nameRes,
                "string",
                "com.application.bmiobesity"
            )
            val idErrorNotAvailable = InTimeApp.APPLICATION.resources.getIdentifier(
                "error_data_not_available",
                "string",
                "com.application.bmiobesity"
            )

            favoriteCardImage.setImageResource(idImage)
            favoriteCardTitle.setText(idTitle)
            favoriteCardDate.text = getFormatData("dd.MM.yyyy")
            if(card.description.isNotEmpty()) {
                favoriteCardDetail.text = card.description
            }
            val resultValue = card.value
            val resultColor = card.valueColour

            if (resultValue.isNotEmpty()) {
                if (card.id == "fat_percent") {
                    val split = resultValue.split(Regex(","))
                    favoriteCardValue.text = split[0]
                    if (resultColor.isNotEmpty()) favoriteCardValue.setTextColor(
                        Color.parseColor(
                            resultColor
                        )
                    )
                } else {
                    favoriteCardValue.text = resultValue
                    if (resultColor.isNotEmpty()) favoriteCardValue.setTextColor(
                        Color.parseColor(
                            resultColor
                        )
                    )
                }
            } else {
                favoriteCardValue.setText(idErrorNotAvailable)
                //favoriteCardValue.setTextColor(Color.RED)
            }
            itemView.setOnClickListener {
                if(it.favoriteCardViewDetailView.isVisible){
                    it.favoriteCardViewDetailView.visibility = View.GONE
                }else{
                    it.favoriteCardViewDetailView.visibility = View.VISIBLE
                }
            }
        }
    }

    object ResultCardDiffCallback : DiffUtil.ItemCallback<ResultCard>() {
        override fun areItemsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ResultCard, newItem: ResultCard): Boolean {
            return (oldItem.id == newItem.id && oldItem.value == newItem.value && oldItem.valueColour == newItem.valueColour)
        }
    }
}