package com.sudox.messenger.android.auth.country

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.common.entries.Country
import com.sudox.messenger.android.auth.R
import kotlinx.android.synthetic.main.item_supported_country.view.supportedCountryCodeText
import kotlinx.android.synthetic.main.item_supported_country.view.supportedCountryFlagImageView
import kotlinx.android.synthetic.main.item_supported_country.view.supportedCountryNameText

class AuthCountryAdapter(
        context: Context,
        val countries: List<Country>
) : RecyclerView.Adapter<AuthCountryAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_supported_country, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.let {
        val country = countries[position]

        it.supportedCountryFlagImageView.setImageResource(country.flagImageId)
        it.supportedCountryNameText.setText(country.nameTextId)
        it.supportedCountryCodeText.text = country.getCodeWithPlus()
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}