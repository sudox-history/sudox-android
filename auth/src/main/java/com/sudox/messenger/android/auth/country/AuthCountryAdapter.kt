package com.sudox.messenger.android.auth.country

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.countryItemView.CountryItemView

class AuthCountryAdapter(
        val context: Context,
        val countries: List<Country>
) : RecyclerView.Adapter<AuthCountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CountryItemView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.let {
        val country = countries[position]

        holder.view.let { view ->
            view.setFlag(country.flagImageId)
            view.setName(country.nameTextId)
            view.setCode(country.countryCode)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    class ViewHolder(val view: CountryItemView) : RecyclerView.ViewHolder(view)
}