package com.application.bmiobesity.view.mainActivity.home.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeFavoriteFragmentBinding
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.viewModels.MainViewModel
import okhttp3.internal.notify

class HomeFavoriteFragment : Fragment(R.layout.main_home_favorite_fragment) {

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
    }

    private fun onClickResultCardMenu(card: ResultCard){

    }

    override fun onDestroyView() {
        favoriteBinding = null
        super.onDestroyView()
    }
}