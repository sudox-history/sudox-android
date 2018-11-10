package com.sudox.android.ui.main.profile.info

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.profile.info.adapter.ProfileParametersAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile_info.*
import javax.inject.Inject

class InfoFragment @Inject constructor(): DaggerFragment() {

    @Inject
    lateinit var profileParametersAdapter: ProfileParametersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileParametersAdapter.parameters = arrayListOf(
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value")
        )

        profileParameters.layoutManager = LinearLayoutManager(context)
        profileParameters.adapter = profileParametersAdapter
    }
}