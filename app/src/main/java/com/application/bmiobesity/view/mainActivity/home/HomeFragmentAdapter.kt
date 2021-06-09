package com.application.bmiobesity.view.mainActivity.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.application.bmiobesity.view.mainActivity.home.analyze.HomeAnalyzeFragment
import com.application.bmiobesity.view.mainActivity.home.favorite.HomeFavoriteFragment
import com.application.bmiobesity.view.mainActivity.home.recommendation.HomeRecommendationFragment

class HomeFragmentAdapter(fragmentActivity: FragmentActivity, private val titles: List<String>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {HomeFavoriteFragment()}
            1 -> {HomeAnalyzeFragment()}
            2 -> {HomeRecommendationFragment()}
            else -> {HomeFavoriteFragment()}
        }
    }
}