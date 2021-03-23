package com.application.bmiobesity.view.mainActivity.medcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainMedcardFragmentBinding

class MedcardFragment : Fragment(R.layout.main_medcard_fragment) {

    private var medcardBinding: MainMedcardFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medcardBinding = MainMedcardFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        medcardBinding = null
        super.onDestroyView()
    }
}