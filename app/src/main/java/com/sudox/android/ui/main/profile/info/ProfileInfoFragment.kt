package com.sudox.android.ui.main.profile.info

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.formatPhoneByMask
import com.sudox.android.data.database.model.user.User
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.profile.ProfileViewModel
import com.sudox.design.recyclerview.adapters.ParametersAdapter
import com.sudox.design.recyclerview.adapters.ParametersDiffUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile_info.*
import javax.inject.Inject

class ProfileInfoFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val parametersAdapter by lazy { ParametersAdapter(mainActivity) }
    private val profileViewModel by lazy { getViewModel<ProfileViewModel>(viewModelFactory) }
    private val mainActivity by lazy { activity as MainActivity }
    private var isLoaded: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Configure views
        initParametersList()

        // Start business-logic
        profileViewModel.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }

    private fun initParametersList() {
        profileInfoParametersList.adapter = parametersAdapter
        profileInfoParametersList.layoutManager = LinearLayoutManager(context!!)
        profileInfoParametersList.itemAnimator = null
        profileInfoParametersList.layoutAnimation = null

        // Listen data
        profileViewModel
                .userLiveData
                .observe(this, Observer { updateParameters(it!!) })
    }

    private fun updateParameters(user: User) {
        val parameters = buildParameters(user)
        val diffUtil = ParametersDiffUtil(parameters, parametersAdapter.parameters)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        // Update
        parametersAdapter.parameters = parameters
        profileInfoParametersList
                .viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (!isLoaded) {
                            profileInfoContentGroup.visibility = View.VISIBLE
                            isLoaded = false

                            // Drawed!
                            profileInfoParametersList
                                    .viewTreeObserver
                                    .removeOnGlobalLayoutListener(this)
                        }
                    }
                })

        // Notify adapter about updates
        diffResult.dispatchUpdatesTo(parametersAdapter)
    }

    private fun buildParameters(user: User): ArrayList<ParametersAdapter.Parameter> {
        val parameters = arrayListOf<ParametersAdapter.Parameter>()

        // Add nickname
        parameters.plusAssign(ParametersAdapter.Parameter(
                iconRes = R.drawable.ic_emoticon,
                name = getString(R.string.nickname),
                value = "@${user.nickname}"))

        // Add phone
        parameters.plusAssign(ParametersAdapter.Parameter(
                iconRes = R.drawable.ic_phone,
                name = getString(R.string.phone_number),
                value = formatPhoneByMask(user.phone!!)))

        // Add bio if installed
        if (user.bio != null) {
            parameters.plusAssign(ParametersAdapter.Parameter(
                    iconRes = R.drawable.ic_info,
                    name = getString(R.string.bio),
                    value = user.bio!!))
        }

        // Return
        return parameters
    }
}