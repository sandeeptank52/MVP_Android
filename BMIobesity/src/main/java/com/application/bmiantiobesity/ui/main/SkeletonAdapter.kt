package com.application.bmiantiobesity.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R


class SkeletonAdapter (private val count: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SimpleRcvViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.main_recomendation_skeleton_recycler, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){} //SimpleRcvViewHolder, position: Int) {}
    override fun getItemCount() = count
}

class SimpleRcvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
   // private val views: SparseArray<View> = SparseArray<View>()

    /*fun <V : View> getView(resId: Int): View {
        var v: View = views[resId]
        if (null == v) {
            v = itemView.findViewById(resId)
            views.put(resId, v)
        }
        return v
    }*/
}