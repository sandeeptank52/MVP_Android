package com.application.bmiobesity.view.mainActivity.profile.country

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.databinding.MainProfileCountryCardViewItemBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import java.util.*
import kotlin.collections.ArrayList

class ProfileCountryAdapterRecycler(
    var data: List<Countries>? = null,
    val onClick: (Countries) -> Unit
) : RecyclerView.Adapter<ProfileCountryAdapterRecycler.ProfileCountryViewHolder>(), Filterable {

    val originalList = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCountryViewHolder {
        return ProfileCountryViewHolder(
            MainProfileCountryCardViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileCountryViewHolder, position: Int) {
        data?.get(position)?.let { holder.bind(it) }
    }

    inner class ProfileCountryViewHolder(
        val binding: MainProfileCountryCardViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(country: Countries) {
            // Set country name
            binding.profileCountryItemTextViewCountryName.text = country.value
            binding.root.setOnClickListener {
                onClick(country)
            }
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                if (constraint.isEmpty()) {
                    results.values = data
                    results.count = data!!.size
                } else {
                    val list: ArrayList<Countries> = ArrayList()
                    for (p in data!!) {
                        if (p.value.uppercase(Locale.getDefault()).contains(
                                constraint.toString()
                                    .uppercase(Locale.getDefault())
                            )
                        ) list.add(p)
                    }
                    results.values = list
                    results.count = list.size
                }
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.count == 0 || constraint == "") {
                    data = originalList
                    notifyDataSetChanged()
                } else {
                    data = results.values as ArrayList<Countries>?
                    notifyDataSetChanged()
                }
            }
        }
    }


}