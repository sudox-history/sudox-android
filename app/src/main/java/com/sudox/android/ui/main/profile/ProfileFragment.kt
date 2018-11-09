package com.sudox.android.ui.main.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.main.profile.adapter.ProfileParametersAdapter
import kotlinx.android.synthetic.main.fragment_main_profile.*
import javax.inject.Inject

class ProfileFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var profileParametersAdapter: ProfileParametersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileParametersAdapter.parameters = arrayListOf(
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_message, "Name", "Value")
        )

        profileParameters.adapter = profileParametersAdapter
        profileParameters.layoutManager = LinearLayoutManager(context)
    }

    override fun showConnectionStatus(isConnect: Boolean) {

    }
}