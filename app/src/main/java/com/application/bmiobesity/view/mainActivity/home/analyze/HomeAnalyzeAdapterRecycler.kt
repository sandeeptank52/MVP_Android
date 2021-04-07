package com.application.bmiobesity.view.mainActivity.home.analyze

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.application.bmiobesity.model.retrofit.ResultDiseaseRisk

class HomeAnalyzeAdapterRecycler(private val onClick: (ResultDiseaseRisk) -> Unit) : ListAdapter<ResultDiseaseRisk, HomeAnalyzeAdapterRecycler.HomeAnalyzeViewHolder>(HomeAnalyzeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAnalyzeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_home_analyze_card_view, parent, false)
        return HomeAnalyzeViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeAnalyzeViewHolder, position: Int) {
        val risk = getItem(position)
        holder.bind(risk)
    }

    class HomeAnalyzeViewHolder(itemView: View, val onClick: (ResultDiseaseRisk) -> Unit) : RecyclerView.ViewHolder(itemView){
        private var currentResultRisk: ResultDiseaseRisk? = null
        private val message: TextView = itemView.findViewById(R.id.analyzeCardViewText)
        private val card: CardView = itemView.findViewById(R.id.analyzeCardView)

        init {
            itemView.setOnClickListener {
                currentResultRisk?.let {
                    onClick(it)
                }
            }
        }

        fun bind(risk: ResultDiseaseRisk){
            currentResultRisk = risk

            val msg = risk.message
            val color = risk.risk_string

            if (!msg.isNullOrEmpty()){
                message.text = msg
                if (!color.isNullOrEmpty()) card.setCardBackgroundColor(Color.parseColor(color))
            }
        }
    }

    object HomeAnalyzeDiffCallback : DiffUtil.ItemCallback<ResultDiseaseRisk>(){
        override fun areItemsTheSame(
            oldItem: ResultDiseaseRisk,
            newItem: ResultDiseaseRisk
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ResultDiseaseRisk,
            newItem: ResultDiseaseRisk
        ): Boolean {
            return (oldItem.message == newItem.message && oldItem.risk_string == newItem.risk_string)
        }
    }
}