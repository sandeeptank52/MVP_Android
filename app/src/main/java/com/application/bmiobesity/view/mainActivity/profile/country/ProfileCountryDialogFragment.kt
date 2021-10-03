package com.application.bmiobesity.view.mainActivity.profile.country

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileCountryDialogFragmentBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ProfileCountryDialogFragment(
    private val initialCountry: Countries? = null,
    private val onClick: (Countries) -> Unit
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
        adapter = ProfileCountryAdapterRecycler(initialCountry, mainModel.countries.filter { c -> c.value.isNotEmpty()})
        binding?.profileCountryDialogRecyclerCountries?.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding?.profileCountryDialogCardViewClose?.clicks()?.subscribe {
            dismiss()
        }

        binding?.profileCountryDialogButtonOk?.clicks()?.subscribe {
            if (adapter.selectedCountry == null || adapter.selectedCountry!!.id == 0) {
                Snackbar.make(
                    binding!!.root,
                    resources.getString(R.string.please_select_your_country),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                adapter.selectedCountry?.let { country -> onClick(country) }
                dismiss()
            }
        }

    }

    private fun addRx() {
        allDisposable = CompositeDisposable()

        val searchDisposable = binding?.profileCountryDialogEditTextSearch?.textChanges()
            ?.subscribe {
                adapter.filter!!.filter(it.toString())
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