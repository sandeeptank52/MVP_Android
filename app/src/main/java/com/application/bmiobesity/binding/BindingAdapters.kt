package com.application.bmiobesity.binding

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @BindingAdapter("android:onClick")
    @JvmStatic
    fun setOnClick(
        view: View, clickListener: View.OnClickListener?,
    ) {
        view.setOnClickListener(clickListener)
    }

}