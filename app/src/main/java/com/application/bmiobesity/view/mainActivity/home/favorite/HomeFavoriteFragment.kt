package com.application.bmiobesity.view.mainActivity.home.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeFavoriteFragmentBinding

class HomeFavoriteFragment : Fragment(R.layout.main_home_favorite_fragment) {

    private var favoriteBinding: MainHomeFavoriteFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteBinding = MainHomeFavoriteFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        favoriteBinding = null
        super.onDestroyView()
    }
}