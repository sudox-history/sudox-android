package com.sudox.design.recyclerview.adapters

import androidx.recyclerview.widget.DiffUtil

class ParametersDiffUtil(val newParameters: ArrayList<ParametersAdapter.Parameter>,
                         val oldParameters: ArrayList<ParametersAdapter.Parameter>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newParameters[newItemPosition].name == oldParameters[oldItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newParameters[newItemPosition] == oldParameters[oldItemPosition]
    }

    override fun getOldListSize(): Int = oldParameters.size
    override fun getNewListSize(): Int = newParameters.size
}