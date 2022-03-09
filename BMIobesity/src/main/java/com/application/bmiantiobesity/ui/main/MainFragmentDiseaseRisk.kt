package com.application.bmiantiobesity.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.controls.RiskViewHeight
import com.application.bmiantiobesity.retrofit.DiseaseRisk
import com.application.bmiantiobesity.retrofit.Result
import kotlinx.android.synthetic.main.main_fragment_disease_risk.view.*

class MainFragmentDiseaseRisk : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragmentDiseaseRisk()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_disease_risk, container, false)

        // Initialize
        MainViewModel.updateSingleResult.observe(
            this.requireActivity(),
            androidx.lifecycle.Observer { createDiseaseRecyclerView(view, it) })

        return view
    }


    private fun createDiseaseRecyclerView(view: View, result: Result) {

        // Загрузка данных при обновлении и Подключение RecycleView
        val recyclerView = view.findViewById<RecyclerView>(R.id.main_risk)

        if (!result.disease_risk.isNullOrEmpty()) {
            view.main_text_selection.isVisible = true
            view.main_risk_divider.isVisible = true


            //recyclerView.setHasFixedSize(false)
            //recyclerView.layoutManager = GridLayoutManager(context, 2)//LinearLayoutManager(context)
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            val myDataAdapter = DiseaseRiskSetAdapter(
                result.disease_risk!!,
                object : DiseaseRiskSetAdapter.Callback {
                    override fun onItemClicked(item: DiseaseRisk) {
                        //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                        //Snackbar.make(view, item.message ?: "", Snackbar.LENGTH_LONG).show()
                        Toast.makeText(context, item.recomendation, Toast.LENGTH_SHORT).show()
                        //dbAction.delete(item)
                    }
                })

            recyclerView.adapter = myDataAdapter
            myDataAdapter.notifyDataSetChanged()
            // Для сохранение отредактированных измененний.
            recyclerView.setItemViewCacheSize(result.disease_risk!!.size)

        } else {
            view.main_text_selection.isVisible = false
            view.main_risk_divider.isVisible = false

            recyclerView.adapter = null
            //myDataAdapter.notifyDataSetChanged()
        }
    }

}

class DiseaseRiskSetAdapter(var items: List<DiseaseRisk>, val callback: Callback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderRisk(
        LayoutInflater.from(parent.context).inflate(R.layout.disease_risk_recycler, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderRisk -> holder.bind(items[position])
            else -> throw IllegalArgumentException("Unknown Holder")
        }
    }

    inner class HolderRisk(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textRisk: TextView = itemView.findViewById(R.id.diseases_text)
        private val descriptionRisk: TextView = itemView.findViewById(R.id.disease_persent)
        private val viewRisk: RiskViewHeight = itemView.findViewById(R.id.diseases_risk)

        fun bind(item: DiseaseRisk) {
            textRisk.text = item.message

            val str = if (item.risk_percents == null) ""
            else if (item.risk_percents!!.contains('%')) "${item.risk_percents}"
            else "${item.risk_percents} %"

            if (str.isNotEmpty()) descriptionRisk.text = str else descriptionRisk.isVisible = false

            //viewRisk.setPercent(item.risk_percents ?: -1f) // uncomment
            viewRisk.setStringColor(item.risk_string ?: "#FF2D55")


            val onClickListener = View.OnClickListener {
                //item.contentFirst = editText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            descriptionRisk.setOnClickListener(onClickListener)
            //viewRisk.setOnClickListener (onClickListener)
            itemView.setOnClickListener(onClickListener)
        }
    }

    interface Callback {
        fun onItemClicked(item: DiseaseRisk)
    }
}