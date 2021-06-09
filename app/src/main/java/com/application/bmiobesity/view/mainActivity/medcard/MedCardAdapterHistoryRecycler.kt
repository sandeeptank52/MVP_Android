package com.application.bmiobesity.view.mainActivity.medcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSimpleValue

class MedCardAdapterHistoryRecycler: ListAdapter<MedCardParamSimpleValue, MedCardAdapterHistoryRecycler.HistoryViewHolder>(HistoryDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_medcard_history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var currentSimpleValue: MedCardParamSimpleValue? = null
        private val value: TextView = itemView.findViewById(R.id.mainMedCardHistoryValue)
        private val date: TextView = itemView.findViewById(R.id.mainMedCardHistoryDate)

        fun bind(item: MedCardParamSimpleValue){
            currentSimpleValue = item
            value.text = item.value?.toString()
            date.text = item.timestamp.toString()
        }
    }

    object HistoryDiffCallBack: DiffUtil.ItemCallback<MedCardParamSimpleValue>(){
        override fun areItemsTheSame(oldItem: MedCardParamSimpleValue, newItem: MedCardParamSimpleValue): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MedCardParamSimpleValue, newItem: MedCardParamSimpleValue): Boolean {
            return oldItem.id == newItem.id
        }
    }
}