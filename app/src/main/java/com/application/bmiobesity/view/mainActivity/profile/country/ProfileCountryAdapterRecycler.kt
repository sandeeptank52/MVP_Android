package com.application.bmiobesity.view.mainActivity.profile.country

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileCountryCardViewItemBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries

class ProfileCountryAdapterRecycler(
    var selectedCountry: Countries? = null
) : ListAdapter<Countries, ProfileCountryAdapterRecycler.ProfileCountryViewHolder>(
    ProfileCountryDiffCallback
) {

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
        val country = getItem(position)
        holder.bind(country)
    }

    inner class ProfileCountryViewHolder(
        val binding: MainProfileCountryCardViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(country: Countries) {
            // Set card background for selected country
            if (selectedCountry != null && country.id == selectedCountry?.id) {
                binding.profileCountryItemCardViewContainer.setCardBackgroundColor(
                    InTimeApp.APPLICATION.getColor(
                        R.color.colorPrimary
                    )
                )
                binding.profileCountryItemTextViewCountryName.setTextColor(
                    InTimeApp.APPLICATION.getColor(
                        R.color.color_white
                    )
                )
            } else {
                binding.profileCountryItemCardViewContainer.setCardBackgroundColor(
                    InTimeApp.APPLICATION.getColor(
                        R.color.color_white
                    )
                )
                binding.profileCountryItemTextViewCountryName.setTextColor(
                    InTimeApp.APPLICATION.getColor(
                        R.color.color_black
                    )
                )
            }

            // Set country name
            binding.profileCountryItemTextViewCountryName.text = country.value
            binding.root.setOnClickListener {
                selectedCountry = country
                notifyDataSetChanged()
            }
        }
    }

    object ProfileCountryDiffCallback : DiffUtil.ItemCallback<Countries>() {

        override fun areItemsTheSame(oldItem: Countries, newItem: Countries): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Countries, newItem: Countries): Boolean {
            return oldItem.id == newItem.id && oldItem.value == newItem.value
        }

    }

}