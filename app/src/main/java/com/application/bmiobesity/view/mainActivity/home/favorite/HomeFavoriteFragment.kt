package com.application.bmiobesity.view.mainActivity.home.favorite

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.MainHomeFavoriteFragmentBinding
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

class HomeFavoriteFragment : BaseFragment(R.layout.main_home_favorite_fragment) {

    private var favoriteBinding: MainHomeFavoriteFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteBinding = MainHomeFavoriteFragmentBinding.bind(view)
        init()
    }

    private fun init(){
        val favoriteAdapter = HomeFavoriteAdapterRecycler { onClickResultCardMenu(it) }
        favoriteBinding?.favoriteRecyclerResultCard?.adapter = favoriteAdapter
        mainModel.resultCard.observe(viewLifecycleOwner, {
            it?.let {
                favoriteAdapter.submitList(it as MutableList<ResultCard>)
                favoriteAdapter.notifyDataSetChanged()
            }
        })
        lifecycleScope.launch(Dispatchers.IO) {
            val firstTime = mainModel.isFirstTimeAsync().await()
            if (firstTime){
                withContext(Dispatchers.Main) {
                    val bundle = bundleOf("isFirstTime" to true)
                    findNavController().navigate(R.id.mainNavHomeToProfile, bundle)
                }
            }
        }
    }

    private fun onClickResultCardMenu(card: ResultCard){
        if (card.description.isNotEmpty()){
            Toast.makeText(requireContext(), card.description, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        favoriteBinding = null
        super.onDestroyView()
    }
}