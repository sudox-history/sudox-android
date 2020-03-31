package com.sudox.messenger.android.auth.country

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.countryItemView.CountryItemView
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.auth.R

class AuthCountryAdapter(
        val context: Context,
        val countries: List<Country>
) : ViewListAdapter<AuthCountryAdapter.ViewHolder>() {

    var clickCallback: ((Country) -> (Unit))? = null

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CountryItemView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        val country = countries[holder.adapterPosition]

        holder.view.let { view ->
            view.setFlag(country.flagImageId)
            view.setName(country.nameTextId)
            view.setCode(country.countryCode)
            view.setOnClickListener {
                clickCallback?.invoke(countries[holder.adapterPosition])
            }
        }
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.authcountryadapter_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return countries.size
    }

    class ViewHolder(val view: CountryItemView) : RecyclerView.ViewHolder(view)
}