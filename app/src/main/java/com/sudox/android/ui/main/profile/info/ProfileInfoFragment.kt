package com.sudox.android.ui.main.profile.info

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.profile.info.adapter.ProfileParametersAdapter
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile_info.*
import javax.inject.Inject

class ProfileInfoFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var profileParametersAdapter: ProfileParametersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProfileParametersList()
    }

    private fun initProfileParametersList() {
        profileParameters.layoutManager = LinearLayoutManager(context)
        profileParameters.addItemDecoration(SecondColumnItemDecorator(context!!, false))

        profileParametersAdapter.parameters = arrayListOf(
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_emoticon, getString(R.string.nickname), "@themax"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_alternate_email, getString(R.string.email), "maksim182003mit@gmail.com"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_info, getString(R.string.profile_info), "Информация о себе"),
                ProfileParametersAdapter.ProfileParameter(R.drawable.ic_public, getString(R.string.website), "https://themax.io")
        )

        profileParameters.adapter = profileParametersAdapter
    }
}