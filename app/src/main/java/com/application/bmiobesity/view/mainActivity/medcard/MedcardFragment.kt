package com.application.bmiobesity.view.mainActivity.medcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainMedcardFragmentBinding

class MedcardFragment : Fragment(R.layout.main_medcard_fragment) {

    private var medcardBinding: MainMedcardFragmentBinding? = null

//FIXME: Add as strings xml once the translation is ready
    private val medCardListDataSet = arrayOf<MedCardListItem>(
        MedCardListItem("Weight", "Weight in kg"),
        MedCardListItem("Waist circumference", "Waist circumference in cm"),
        MedCardListItem("Thigh circumference", "Thigh circumference in cm"),
        MedCardListItem("Wrist circumference", "Wrist circumference at thinnest point in cm"),
        MedCardListItem("Neck circumference", "Neck circumference in cm"),
        MedCardListItem("Heart rate", "Pulse in beats per minute"),
        MedCardListItem("Arterial pressure", "Systolic / dystolic pressure in mmHg"),
        MedCardListItem("Glucose level", "Blood glucose level in mmol / l"),
        MedCardListItem("Cholesterol level", "Cholesterol level in blood in mmol / l"),
        MedCardListItem("Physical activity level", "Physical labor or training 3-5 times a week"),

        )

    private val listAdapter = MedcardItemsListAdapter(medCardListDataSet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medcardBinding = MainMedcardFragmentBinding.bind(view)

        val recyclerView = medcardBinding?.medicalDataRv
        recyclerView?.adapter = listAdapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)
    }

    override fun onDestroyView() {
        medcardBinding = null
        super.onDestroyView()
    }
}