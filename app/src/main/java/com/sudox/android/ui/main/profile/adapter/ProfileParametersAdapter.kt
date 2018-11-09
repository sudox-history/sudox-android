package com.sudox.android.ui.main.profile.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import kotlinx.android.synthetic.main.item_profile_parameter.view.*
import javax.inject.Inject

class ProfileParametersAdapter @Inject constructor(private val context: Context) : RecyclerView.Adapter<ProfileParametersAdapter.ViewHolder>() {

    lateinit var parameters: List<ProfileParameter>

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_profile_parameter, viewGroup, false))
    }

    override fun getItemCount(): Int = parameters.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(parameters[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(profileParameter: ProfileParameter) {
            view.profileParameterIcon.setBackgroundResource(profileParameter.iconRes)
            view.profileParameterName.text = profileParameter.name
            view.profileParameterValue.text = profileParameter.value
        }
    }

    data class ProfileParameter(val iconRes: Int, val name: String, val value: String)
}