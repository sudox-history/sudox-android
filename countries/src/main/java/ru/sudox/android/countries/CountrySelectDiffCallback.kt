package ru.sudox.android.countries

import androidx.recyclerview.widget.DiffUtil
import ru.sudox.android.countries.vos.CountryVO

class CountrySelectDiffCallback(
        val oldList: List<CountryVO>,
        val newList: List<CountryVO>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].countryCode == newList[newItemPosition].countryCode
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }
}