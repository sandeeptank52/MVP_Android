package com.application.bmiobesity.view.mainActivity.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.common.eventManagerMain.EventManagerMain
import com.application.bmiobesity.common.eventManagerMain.MainSettingEvent
import com.application.bmiobesity.databinding.MainSettingFragmentBinding
import com.application.bmiobesity.view.labelActivity.LabelActivity
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingFragment : Fragment(R.layout.main_setting_fragment) {

    private var settingBinding: MainSettingFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()
    private val settingEvent: MainSettingEvent = EventManagerMain.getEventManager()

    private lateinit var measureAdapter: ListAdapter
    private lateinit var measureDialog: MaterialAlertDialogBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingBinding = MainSettingFragmentBinding.bind(view)

        init()
        initLayoutListeners()
        initListeners()
    }

    private fun init(){
        val measureArray = arrayListOf<String>(
            getString(R.string.setting_measure_metric),
            getString(R.string.setting_measure_imperial)
        )
        measureAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice, measureArray)
        initMeasureDialog(0)
    }

    private fun initLayoutListeners(){
        settingBinding?.settingMeasureConstraint?.setOnClickListener { measureDialog.show() }
        settingBinding?.constraintDoctor?.setOnClickListener { showComingSoon() }
        settingBinding?.constraintConnections?.setOnClickListener { showComingSoon() }
        settingBinding?.constraintSupport?.setOnClickListener { showComingSoon() }
        settingBinding?.constraintMore?.setOnClickListener { showComingSoon() }
        settingBinding?.constraintDelete?.setOnClickListener { showDeleteDialog() }
    }

    private fun initListeners(){
        settingEvent.getStartUserDeleting().observe(viewLifecycleOwner, EventObserver{
            if (it) settingBinding?.settingDeleteProgress?.visibility = View.VISIBLE
        })
        settingEvent.getEndUserDeleting().observe(viewLifecycleOwner, EventObserver{
            if (it){
                settingBinding?.settingDeleteProgress?.visibility = View.GONE
                val intent = Intent(requireContext(), LabelActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        })
        mainModel.profileManager.currentMeasurementSystem.observe(viewLifecycleOwner, {
            it?.let {
                initMeasureDialog(it.id - 1)
            }
        })
    }

    private fun showDeleteDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setPositiveButton(R.string.button_delete){_, _ ->
            mainModel.deleteProfile()
        }
        dialog.setNegativeButton(R.string.button_cancel){_, _ ->}
        dialog.setTitle(R.string.setting_delete_dialog_title)
        dialog.setMessage(R.string.setting_delete_dialog_message)
        dialog.show()
    }
    private fun initMeasureDialog(item: Int){
        measureDialog = MaterialAlertDialogBuilder(requireContext())
        measureDialog.setTitle(R.string.setting_measure_dialog_title)
        measureDialog.setSingleChoiceItems(measureAdapter, item){dialog, which ->
            when (which){
                0 -> {mainModel.profileManager.setMeasuringSystem(MeasuringSystem.METRIC)}
                1 -> {mainModel.profileManager.setMeasuringSystem(MeasuringSystem.IMPERIAL)}
            }
            dialog.dismiss()
        }
    }

    private fun showComingSoon(){
        Toast.makeText(requireContext(), "COMING SOON", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        settingBinding = null
        super.onDestroyView()
    }
}