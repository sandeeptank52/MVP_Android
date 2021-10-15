package com.application.bmiobesity.view.mainActivity.profile.country

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileCountryDialogFragmentBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.viewModels.MainViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ProfileCountryDialogFragment(
    val onClick: (Countries) -> Unit
) : DialogFragment(R.layout.main_profile_country_dialog_fragment) {

    private var binding: MainProfileCountryDialogFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()
    private lateinit var allDisposable: CompositeDisposable
    private lateinit var adapter: ProfileCountryAdapterRecycler

    override fun getTheme(): Int {
        return R.style.RoundCornerDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainProfileCountryDialogFragmentBinding.bind(view)

        initAdapter()
        initListener()
        addRx()
    }

    private fun initAdapter() {
        adapter = ProfileCountryAdapterRecycler(
            mainModel.countries.filter { c -> c.value.isNotEmpty() }
        ) { country ->
            onClick(country)
            dismiss()
        }
        binding?.profileCountryDialogRecyclerCountries?.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding?.profileCountryDialogCardViewClose?.clicks()?.subscribe { dismiss() }
    }

    private fun addRx() {
        allDisposable = CompositeDisposable()

        val searchDisposable = binding?.profileCountryDialogEditTextSearch?.textChanges()
            ?.subscribe {
                adapter.filter.filter(it.toString())
            }

        allDisposable.addAll(searchDisposable)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (!allDisposable.isDisposed) allDisposable.dispose()
        super.onDestroy()
    }
}