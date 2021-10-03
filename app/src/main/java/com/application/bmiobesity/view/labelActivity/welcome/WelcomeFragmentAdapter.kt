package com.application.bmiobesity.view.labelActivity.welcome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WelcomeFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> {WelcomeFragment1()}
            1 -> {WelcomeFragment2()}
            2 -> {WelcomeFragment3()}
            else -> {WelcomeFragment1()}
        }
    }
}