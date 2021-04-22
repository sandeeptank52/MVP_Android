package com.application.bmiobesity.view.mainActivity.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileFragmentBinding
import java.util.*


class ProfileFragment : Fragment(R.layout.main_profile_fragment) {

    private var profileBinding: MainProfileFragmentBinding? = null
    private var datePickerDialog: DatePickerDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileFragmentBinding.bind(view)

        profileBinding?.genderSpinner
        setUpDatePicker()
    }


    private fun setUpDatePicker() {
        profileBinding?.birthDatePicker?.setOnClickListener {
            // calender class's instance and get current date , month and year from calender
            // calender class's instance and get current date , month and year from calender
            val c: Calendar = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR) // current year

            val mMonth: Int = c.get(Calendar.MONTH) // current month

            val mDay: Int = c.get(Calendar.DAY_OF_MONTH) // current day

            // date picker dialog
            // date picker dialog
            datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    profileBinding?.birthDatePicker?.setText(dayOfMonth.toString() + "/"
                            + (monthOfYear + 1) + "/" + year)
                    datePickerDialog?.dismiss()
                }, mYear, mMonth, mDay
            )
            datePickerDialog?.show()
        }
    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }
}
