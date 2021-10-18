package com.application.bmiobesity.view.mainActivity.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.MainServicesFragmentBinding
import com.application.bmiobesity.viewModels.MainViewModel

class ServicesFragment : DialogFragment(R.layout.main_services_fragment) {

    private var servicesBinding: MainServicesFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        servicesBinding = MainServicesFragmentBinding.bind(view)
        servicesBinding?.vm = mainModel
        servicesBinding?.lifecycleOwner = this

        // TODO: init services section
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onDestroyView() {
        servicesBinding = null
        super.onDestroyView()
    }

}