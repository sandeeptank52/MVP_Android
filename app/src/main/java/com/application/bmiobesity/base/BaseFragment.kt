package com.application.bmiobesity.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.application.bmiobesity.InTimeApp
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {
    override fun onResume() {
        super.onResume()
        setCurrentScreen(this.javaClass.simpleName)
    }
}

private fun setCurrentScreen(screenName: String) = FirebaseAnalytics.getInstance(InTimeApp.APPLICATION).run {
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
    bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
    logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}