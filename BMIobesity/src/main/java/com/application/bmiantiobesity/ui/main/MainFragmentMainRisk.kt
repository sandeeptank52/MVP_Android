package com.application.bmiantiobesity.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.usersettings.ConfigToDisplay
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import kotlinx.android.synthetic.main.main_fragment_main_risk.view.*
import kotlinx.android.synthetic.main.main_recomendation_button.view.*

class MainFragmentMainRisk() : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragmentMainRisk()
    }

    private val viewModel by viewModels<MainViewModel>() // lazy create
    private lateinit var userConfigToDisplay: List<ConfigToDisplay>
    private lateinit var skeletonBuilder: RecyclerViewSkeletonScreen.Builder
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_main_risk, container, false)

        // Initialize
        initSkeletonLoading(view)

        // Первый запуск загрузки данных c учётом полученных настроек
        viewModel.getUserSettings(this.requireActivity())
            .observe(this.requireActivity(), androidx.lifecycle.Observer {
                userConfigToDisplay = it
                //swipeListener.onRefresh()
            })

        MainViewModel.isSkeletonLoading.observe(
            this.requireActivity(),
            androidx.lifecycle.Observer {
                if (it) showSkeletonLoading()
                else {
                    hideSkeletonLoading()
                    //MainViewModel.singleResult?.let { createMainRecyclerView(view) }
                }
            })

        MainViewModel.updateDataAdapter.observe(
            this.requireActivity(),
            androidx.lifecycle.Observer {
                createMainRecyclerView(view, it)
            })
        MainViewModel.isNeedToShowEditDialog.observe(this.requireActivity()) {
            if (it == true) {
                showSettingsDialog()
                viewModel.setShowFalse()
            }
        }

        // Редактирование избранного
        view.main_edit_textview.setOnClickListener { showSettingsDialog() } // раскомитить }

        return view
    }

    private fun showSkeletonLoading() {
        skeletonScreen = skeletonBuilder.show()
    }

    private fun hideSkeletonLoading() {
        skeletonScreen.hide()
    }

    private fun initSkeletonLoading(view: View) {
        //Skeleton
        val recyclerSkeleton = view.findViewById<RecyclerView>(R.id.main_dashboard)
        recyclerSkeleton.layoutManager = GridLayoutManager(context,2)
        val countSkeleton = 7
        val adapter = SkeletonAdapter(countSkeleton)
        skeletonBuilder = Skeleton.bind(recyclerSkeleton)
            .adapter(adapter)
            .shimmer(true)
            .angle(20)
            .frozen(false)
            .duration(1200)
            .count(countSkeleton)
            .load(R.layout.main_recomendation_skeleton_recycler)
    }


    private fun createMainRecyclerView(view: View, mainRiskSetAdapter: MainRiskSetAdapter) {

        view.main_dashboard.layoutManager = GridLayoutManager(context, 2)
        view.main_dashboard.adapter = mainRiskSetAdapter

        mainRiskSetAdapter.notifyDataSetChanged()
    }

    // Диалог выбора настроек
    private fun showSettingsDialog() {
        val editSheetDialogFragment = EditBottomSheet.newInstance()
        editSheetDialogFragment.setDataList(userConfigToDisplay)
        editSheetDialogFragment.setSheetListener(object : EditSheetListener {
            override fun onDataSelected(items: List<ConfigToDisplay>) {
                try {
                    MainViewModel.updateSwipeRefresh.value = true
                } catch (ex: Exception) {

                }
                try {
                    viewModel.updateConfigToDBAll(items)
                } catch (ex: Exception) {

                }
            }
        })
        editSheetDialogFragment.show(parentFragmentManager, "EditSheet")
    }
}

class MainRiskSetAdapter(var items: List<MainResult>, val callback: Callback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Определение типа сообщения
    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            TypeOfInformation.UNFILLED_BUTTON -> 0
            else -> 1
        }
    }

    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderRisk(LayoutInflater.from(parent.context).inflate(R.layout.main_recomendation_recycler, parent, false))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> HolderButton(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.main_recomendation_button, parent, false)
        )
        else -> HolderRisk(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.main_recomendation_recycler_updated, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderRisk -> holder.bind(items[position])
            is HolderButton -> holder.bind(items[position])
            else -> throw IllegalArgumentException("Unknown Holder")
        }
    }

    inner class HolderButton(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MainResult) {
            val onClickListener = View.OnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            itemView.main_recomendation_button.text = item.description
            itemView.main_recomendation_button.setOnClickListener(onClickListener)
            //itemView.setOnClickListener (onClickListener)
        }
    }

    inner class HolderRisk(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textRisk: TextView = itemView.findViewById(R.id.recomendation_text)
        private val descriptionRisk: TextView =
            itemView.findViewById(R.id.recomendation_information)
        private val dateText: TextView = itemView.findViewById(R.id.recomendation_data)
        private val recommendationsImage: ImageView =
            itemView.findViewById(R.id.recomendation_imageView)

        fun bind(item: MainResult) {
            textRisk.text = item.description
            descriptionRisk.text = item.information
            dateText.text = item.date
            //viewRisk.setPercent(67)


            descriptionRisk.setTextColor(Color.parseColor(item.color))
//            cvMain.setBackgroundColor(Color.parseColor(item.color))
            // Установка значков
            when (item.type) {
                TypeOfInformation.BMI -> recommendationsImage.setImageResource(R.drawable.ic_bmi)
                TypeOfInformation.OBESITY_LEVEL -> recommendationsImage.setImageResource(R.drawable.ic_obesity_level)
                TypeOfInformation.COMMON_RISK_LEVEL -> recommendationsImage.setImageResource(R.drawable.ic_common_risk_level)
                TypeOfInformation.BASE_METABOLISM -> recommendationsImage.setImageResource(R.drawable.ic_base_metabolism)
                TypeOfInformation.BIO_AGE -> recommendationsImage.setImageResource(R.drawable.ic_biological_age)
                //TypeOfInformation.CALORIES_TO_LOW_WEIGHT -> recomendationsImage.setImageResource(R.drawable.ic_universal_icon)
                TypeOfInformation.IDEAL_WEIGHT -> recommendationsImage.setImageResource(R.drawable.ic_ideal_w)
                TypeOfInformation.PROGNOSTIC_AGE -> recommendationsImage.setImageResource(R.drawable.ic_prognostic_age)
                TypeOfInformation.WAIST_TO_HIP_PROPORTIONS -> recommendationsImage.setImageResource(
                    R.drawable.ic_waist_to_hip
                )
                TypeOfInformation.BODY_TYPE -> recommendationsImage.setImageResource(R.drawable.ic_body_type)
                TypeOfInformation.ERROR -> recommendationsImage.setImageResource(R.drawable.ic_icon_exclam)
                TypeOfInformation.FAT_PERCENT -> recommendationsImage.setImageResource(R.drawable.ic_fat_persent)
                else -> recommendationsImage.setImageResource(R.drawable.ic_universal_icon)
            }


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
        fun onItemClicked(item: MainResult)
    }
}

