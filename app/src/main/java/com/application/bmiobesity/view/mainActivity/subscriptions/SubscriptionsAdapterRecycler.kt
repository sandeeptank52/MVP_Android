package com.application.bmiobesity.view.mainActivity.subscriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.services.google.billing.SkuDetailConfig
import com.application.bmiobesity.utils.Duration8601
import java.time.Duration
import java.util.*

class SubscriptionsAdapterRecycler(private val subsAction: (SkuDetailConfig) -> Unit) : ListAdapter<SkuDetailConfig, SubscriptionsAdapterRecycler.SubsViewHolder>(SubsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_subs_card_item, parent, false)
        return SubsViewHolder(view, subsAction)
    }

    override fun onBindViewHolder(holder: SubsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class SubsViewHolder(itemView: View, private val subsAction: (SkuDetailConfig) -> Unit): RecyclerView.ViewHolder(itemView){
        private var currentSub: SkuDetailConfig? = null
        private var subTitle: TextView = itemView.findViewById(R.id.subsTitle)
        private var subDescription: TextView = itemView.findViewById(R.id.subsDescription)
        private var subCost: TextView = itemView.findViewById(R.id.subsCostValue)
        private var subButton: AppCompatButton = itemView.findViewById(R.id.subsButtonAction)

        fun bind(item: SkuDetailConfig){
            currentSub = item
            subTitle.text = item.title
            subDescription.text = item.description


            if (item.isPurchased){
                subCost.text = InTimeApp.APPLICATION.getString(R.string.subs_text_activated)
                subButton.text = InTimeApp.APPLICATION.getString(R.string.button_subs_manage)
            } else {
                subCost.text = item.price
                subButton.text = InTimeApp.APPLICATION.getString(R.string.button_subscribe)
            }

            subButton.setOnClickListener { subsAction(item) }

        }
    }

    object SubsDiffCallback: DiffUtil.ItemCallback<SkuDetailConfig>(){
        override fun areItemsTheSame(oldItem: SkuDetailConfig, newItem: SkuDetailConfig): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SkuDetailConfig, newItem: SkuDetailConfig): Boolean {
            return oldItem.sku == newItem.sku
        }
    }
}