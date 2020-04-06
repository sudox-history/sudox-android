package ru.sudox.android.countries

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.android.countries.helpers.COUNTRIES
import ru.sudox.android.countries.views.CountryItemView
import ru.sudox.android.countries.vos.CountryVO

class CountrySelectAdapter(
        val context: Context,
        val clickCallback: (CountryVO) -> (Unit)
) : ViewListAdapter<CountrySelectAdapter.ViewHolder>() {

    private val countries = COUNTRIES.values.sortedBy {
        context.getString(it.nameId)
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CountryItemView(parent.context)).apply {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickCallback(countries[adapterPosition])
                }
            }
        }
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.vo = countries[position]
    }

    override fun buildStickyLettersMap(): Map<Int, String>? {
        val letters = LinkedHashMap<Int, String>()

        for (index in countries.size - 1 downTo 0) {
            val letter = context.getString(countries[index].nameId)[0].toString()

            if (index > 0) {
                val prevLetter = context.getString(countries[index - 1].nameId)[0].toString()

                if (letter != prevLetter) {
                    letters[index] = letter
                }
            } else {
                letters[index] = letter
            }
        }

        return letters
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.countryselectadapter_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return countries.size
    }

    class ViewHolder(val view: CountryItemView) : RecyclerView.ViewHolder(view)
}