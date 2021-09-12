package com.application.bmiobesity.view.mainActivity.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeFragmentBinding
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.main_home_fragment) {

    private var homeBinding: MainHomeFragmentBinding? = null
    private lateinit var titles: List<String>
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding = MainHomeFragmentBinding.bind(view)
        initViewPager()
        init()
    }

    private fun init(){
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

    private fun initViewPager(){
        titles = arrayListOf(getString(R.string.main_home_menu_favorite),
                getString(R.string.main_home_menu_analyze),
                getString(R.string.main_home_menu_recommendations),
                getString(R.string.main_home_menu_report))

        homeBinding?.mainHomeViewPager?.adapter = HomeFragmentAdapter(requireActivity(), titles)
        homeBinding?.mainHomeViewPager?.let {
            homeBinding?.mainHomeTabLayoutMenu?.let { it1 ->
                TabLayoutMediator(it1, it
                ) { tab, position ->
                    tab.text = titles[position]
                }.attach()
            }
        }
        mainModel.selectedIndex.observe(viewLifecycleOwner, {
            homeBinding?.mainHomeTabLayoutMenu?.getTabAt(it)?.select()
        })
    }

    override fun onDestroyView() {
        homeBinding = null
        super.onDestroyView()
    }
}