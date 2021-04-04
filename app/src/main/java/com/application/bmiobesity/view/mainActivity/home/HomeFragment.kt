package com.application.bmiobesity.view.mainActivity.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.main_home_fragment) {

    private var homeBinding: MainHomeFragmentBinding? = null

    private lateinit var titles: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding = MainHomeFragmentBinding.bind(view)
        init()
    }

    private fun init(){
        titles = arrayListOf(getString(R.string.main_home_menu_favorite),
                getString(R.string.main_home_menu_analyze),
                getString(R.string.main_home_menu_recommendations))

        homeBinding?.mainHomeViewPager?.adapter = HomeFragmentAdapter(requireActivity(), titles)
        homeBinding?.mainHomeViewPager?.let {
            homeBinding?.mainHomeTabLayoutMenu?.let { it1 ->
                TabLayoutMediator(it1, it
                ) { tab, position ->
                    tab.text = titles[position]
                }.attach()
            }
        }
    }

    override fun onDestroyView() {
        homeBinding = null
        super.onDestroyView()
    }
}