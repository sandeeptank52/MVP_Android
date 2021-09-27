package com.application.bmiobesity.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.application.bmiobesity.R
import com.bumptech.glide.Glide

object BindingAdapter {
    @BindingAdapter("android:onClick")
    @JvmStatic
    fun setOnClick(
        view: View, clickListener: View.OnClickListener?,
    ) {
        view.setOnClickListener(clickListener)
    }

    @BindingAdapter(value = ["android:imageUrl", "android:circleCrop", "android:placeHolder"] , requireAll = false)
    @JvmStatic
    fun loadImage(view: ImageView, url: String?, circleCrop: Boolean?,placeHolder: Int?) {
        if (!url.isNullOrEmpty()) {
            var glideBuilder = Glide.with(view.context).load(url).placeholder(placeHolder?: R.drawable.avatar_icon)
                if(circleCrop == true){
                    glideBuilder = glideBuilder.circleCrop()
                }
                glideBuilder.into(view)
        }
    }

}