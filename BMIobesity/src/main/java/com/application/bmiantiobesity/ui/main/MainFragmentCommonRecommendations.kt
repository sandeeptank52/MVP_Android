package com.application.bmiantiobesity.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R
import kotlinx.android.synthetic.main.main_recomendation_button.view.*
import java.util.concurrent.atomic.AtomicBoolean

enum class TypeOfAdapter{ DATA, BUTTON, ERROR, UNFILLED}
data class CommonRecommendationsAdapter(val typeOfAdapter: TypeOfAdapter, val message_short: String?, val message_long: String?, val importance_level:String?)

class MainFragmentCommonRecommendations : Fragment() {

    private var myDataAdapter: CommonRecommendationsSetAdapter? = null
            /*=  CommonRecommendationsSetAdapter.getInstance(object : CommonRecommendationsSetAdapter.Callback {
        override fun onItemClicked(item: CommonRecommendationsAdapter) {
            //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

            //Snackbar.make(view, item.message_short ?: "", Snackbar.LENGTH_LONG).show()
            if (item.typeOfAdapter == TypeOfAdapter.BUTTON)
                if (isBillingClientOk and isSupportedSubscription) queryBillings()
                //Toast.makeText(context, item.message_short, Toast.LENGTH_SHORT).show()

            //dbAction.delete(item)
        }
    })*/


    companion object {
        @JvmStatic
        fun newInstance() = MainFragmentCommonRecommendations()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_common_recommendations, container, false)

        // Initialize
        MainViewModel.updateCommonRecommendationsAdapter.observe(this.requireActivity(), androidx.lifecycle.Observer {
            myDataAdapter = it
            initRecyclerView(view, it)
        })
        //initRecyclerView(view)

        // Удалить если есть
        MainViewModel.mediatorLiveData.removeSource(MainViewModel.updateSingleResult)
        MainViewModel.mediatorLiveData.removeSource(MainViewModel.updateRecomendations)


        MainViewModel.mediatorLiveData.addSource(MainViewModel.updateSingleResult) {
            if (it.common_recomendations == null)
                MainViewModel.mediatorLiveData.value =  mutableListOf(CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED,null, null, null))
            else{
                val commonRecommendationsAdapter = mutableListOf<CommonRecommendationsAdapter>()
                it.common_recomendations?.forEach { commonRecommendation ->
                    commonRecommendationsAdapter.add(
                        CommonRecommendationsAdapter(TypeOfAdapter.DATA, commonRecommendation.message_short, commonRecommendation.message_long, commonRecommendation.importance_level)
                    ) }
                MainViewModel.mediatorLiveData.value = commonRecommendationsAdapter
            }
        }

        MainViewModel.mediatorLiveData.addSource(MainViewModel.updateRecomendations) {
            MainViewModel.mediatorLiveData.value =
                it ?: mutableListOf(CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED,null, null, null))
        }

        MainViewModel.mediatorLiveData.observe(this.requireActivity(), androidx.lifecycle.Observer { updateCommonRecyclerView(it) })

        return view
    }

    private fun initRecyclerView(view: View, commonRecommendationsSetAdapter: CommonRecommendationsSetAdapter){
        // Загрузка данных при обновлении и Подключение RecycleView
        val recyclerView = view.findViewById<RecyclerView>(R.id.main_recommendation)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = commonRecommendationsSetAdapter
    }

    private val resultBoolean = AtomicBoolean(true)
    private val recomendationBoolean = AtomicBoolean(true)

    private fun updateCommonRecyclerView(recommendations: MutableList<CommonRecommendationsAdapter>) {

        when {
            recommendations.isEmpty() -> return
            recommendations.first() == CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED,null, null, null) -> return
            recommendations.first() != CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED, null, null, "CLEAR") -> {

                if (resultBoolean.get() and (recommendations.first() == CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, null, "FIRST_RESULT"))) {
                    recommendations.removeAt(0)
                    updateAdapter(recommendations)
                    resultBoolean.set(false)
                }

                if (recomendationBoolean.get() and (recommendations.first() == CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, null, "FIRST_RECOMMENDATION"))) {
                    recommendations.removeAt(0)
                    updateAdapter(recommendations)
                    recomendationBoolean.set(false)
                }

            }
            else -> {
                recomendationBoolean.set(true)
                resultBoolean.set(true)

                val range = myDataAdapter?.itemCount ?: 0
                if (range > 0) {
                    myDataAdapter?.clear()
                    myDataAdapter?.notifyItemRangeRemoved(0, range)
                }
            }
        }
    }

    private fun updateAdapter(recommendations: List<CommonRecommendationsAdapter>) {
        val range = myDataAdapter?.itemCount ?: 0
        myDataAdapter?.addItems(recommendations)
        //myDataAdapter?.notifyDataSetChanged()
        myDataAdapter?.notifyItemRangeChanged(range, recommendations.size)
    }

}

class CommonRecommendationsSetAdapter private constructor(val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private var instance: CommonRecommendationsSetAdapter? = null

        fun getInstance(callback: Callback) =
            if (instance == null) CommonRecommendationsSetAdapter(callback)
            else instance
    }

    private var innerItems: MutableList<CommonRecommendationsAdapter> = mutableListOf()

    override fun getItemCount() = innerItems.size

    //Определение типа сообщения
    override fun getItemViewType(position: Int): Int {
        return when (innerItems[position].typeOfAdapter){
            TypeOfAdapter.DATA, TypeOfAdapter.UNFILLED -> 0
            TypeOfAdapter.ERROR -> 1
            TypeOfAdapter.BUTTON -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(viewType) {
            0 -> HolderRecommendation(LayoutInflater.from(parent.context).inflate(R.layout.common_recomendation_recycler, parent, false))
            1 -> HolderError(LayoutInflater.from(parent.context).inflate(R.layout.main_recomendation_recycler, parent, false))
            2 -> HolderButton(LayoutInflater.from(parent.context).inflate(R.layout.main_recomendation_button, parent, false))
            else -> HolderRecommendation(LayoutInflater.from(parent.context).inflate(R.layout.common_recomendation_recycler, parent, false))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is HolderRecommendation -> holder.bind(innerItems[position])
            is HolderButton -> holder.bind(innerItems[position])
            is HolderError -> holder.bind(innerItems[position])
            else -> throw IllegalArgumentException("Unknown Holder")
        }
    }

    fun addItems(items: List<CommonRecommendationsAdapter>){
        innerItems.addAll(innerItems.size, items)
    }

    fun clear(){
        innerItems.clear()
    }

    inner class HolderButton(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CommonRecommendationsAdapter) {
            val onClickListener = View.OnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(innerItems[adapterPosition])
            }

            itemView.main_recomendation_button.text = item.message_short
            itemView.main_recomendation_button.setOnClickListener (onClickListener)
            //itemView.setOnClickListener (onClickListener)
        }
    }

    inner class HolderError(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textRisk: TextView = itemView.findViewById(R.id.recomendation_text)
        private val descriptionRisk: TextView = itemView.findViewById(R.id.recomendation_information)
        private val dateText: TextView = itemView.findViewById(R.id.recomendation_data)
        private val recommendationsImage: ImageView = itemView.findViewById(R.id.recomendation_imageView)

        fun bind(item: CommonRecommendationsAdapter) {
            textRisk.text = item.message_short
            descriptionRisk.text = item.message_long
            dateText.text = ""//item.date

            // Установка значка
            recommendationsImage.setImageResource(R.drawable.ic_icon_exclam)

            val onClickListener = View.OnClickListener {
                //item.contentFirst = editText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(innerItems[adapterPosition])
            }

            descriptionRisk.setOnClickListener (onClickListener)
            //viewRisk.setOnClickListener (onClickListener)
            itemView.setOnClickListener (onClickListener)
        }
    }

    inner class HolderRecommendation(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textRisk: TextView = itemView.findViewById(R.id.common_recomendation_text)
        private val descriptionRisk: TextView = itemView.findViewById(R.id.common_recomendation_information)
        //private val viewRisk: RiskView = itemView.findViewById(R.id.recomendation_riskView)

        fun bind(item: CommonRecommendationsAdapter) {

            if( item.message_short == null) textRisk.isVisible = false
            else  {
                textRisk.isVisible = true
                textRisk.text = item.message_short
            }

            descriptionRisk.text = item.message_long


            val onClickListener = View.OnClickListener {
                //item.contentFirst = editText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(innerItems[adapterPosition])
            }

            descriptionRisk.setOnClickListener (onClickListener)
            //viewRisk.setOnClickListener (onClickListener)
            itemView.setOnClickListener (onClickListener)
        }
    }

    interface Callback {
        fun onItemClicked(item: CommonRecommendationsAdapter)
    }
}