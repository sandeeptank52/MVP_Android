package com.application.bmiantiobesity.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.usersettings.ConfigToDisplay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

class EditBottomSheet : BottomSheetDialogFragment() {

    var items: List<ConfigToDisplay> = mutableListOf()
    var listener: EditSheetListener? = null

    companion object {

        @JvmStatic
        fun newInstance(): EditBottomSheet {
            val args = Bundle()
            val fragment = EditBottomSheet()
            fragment.arguments = args
            return fragment
        }


    }

    fun setDataList(items: List<ConfigToDisplay>): EditBottomSheet {
        this.items = items
        return this
    }

    fun setSheetListener(listener: EditSheetListener): EditBottomSheet {
        this.listener = listener
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_edit,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settingsAdapter = SettingsAdapter(items)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSettings)
        val tvSave = view.findViewById<MaterialTextView>(R.id.tvSave)
        tvSave.setOnClickListener {
            listener?.onDataSelected(items)
            this.dismissAllowingStateLoss()
        }
        recyclerView.adapter = settingsAdapter
        settingsAdapter.notifyDataSetChanged()
        recyclerView.setHasFixedSize(true)

    }


    class SettingsAdapter(var items: List<ConfigToDisplay>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderSettings(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.data_main_settings_recycler_updated, parent, false)
        )

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is HolderSettings -> holder.bind(items[position], holder.adapterPosition)
                else -> throw IllegalArgumentException("Unknown Holder")
            }
        }

        inner class HolderSettings(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val textParameter: TextView = itemView.findViewById(R.id.data_settings_textView)
            private val clSettingItem: ConstraintLayout = itemView.findViewById(R.id.clSettingItem)

            fun bind(item: ConfigToDisplay, pos: Int) {
                textParameter.text = item.name
                clSettingItem.isActivated = item.value
                textParameter.isSelected = item.value
                clSettingItem.setOnClickListener {
                    item.value = !item.value
                    notifyItemChanged(pos)
                }
            }
        }
    }
}

