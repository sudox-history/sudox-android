package com.sudox.design.recyclerview.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import kotlinx.android.synthetic.main.item_parameter.view.*
import javax.inject.Inject

class ParametersAdapter(private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ParametersAdapter.ViewHolder>() {

    var parameters: ArrayList<Parameter> = arrayListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_parameter, viewGroup, false))
    }

    override fun getItemCount(): Int = parameters.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(parameters[position], position)
    }

    inner class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        fun bind(parameter: Parameter, position: Int) {
            view.parameterFavicon.setBackgroundResource(parameter.iconRes)
            view.parameterNameText.installText(parameter.name)
            view.parameterValueText.installText(parameter.value)

            // Последний элемент в списке, скроем разделитель
            if (position == parameters.size - 1)
                view.parameterDivider.visibility = View.INVISIBLE
        }
    }

    data class Parameter(val iconRes: Int, val name: String, val value: String)
}