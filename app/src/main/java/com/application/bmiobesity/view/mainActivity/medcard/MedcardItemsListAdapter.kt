package com.application.bmiobesity.view.mainActivity.medcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.R
import com.google.android.material.textfield.TextInputLayout

class MedcardItemsListAdapter(private val dataSet: Array<MedCardListItem>) :
    RecyclerView.Adapter<MedcardItemsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bottomTextView: TextView = view.findViewById(R.id.medical_data_unit)
        val textInputLayout: TextInputLayout = view.findViewById(R.id.medical_data_layout_name)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_medical_card, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bottomTextView.text = dataSet[position].bottomText
        viewHolder.textInputLayout.hint = dataSet[position].hint
    }

    override fun getItemCount() = dataSet.size

}