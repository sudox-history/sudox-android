package ru.sudox.android.main.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.dialogs.api.DialogsFeatureApi
import ru.sudox.android.people.api.PeopleFeatureApi
import javax.inject.Inject

/**
 * Фрагмент-контейнер основной части приложения.
 */
@AndroidEntryPoint
class MainFlowFragment : Fragment(R.layout.fragment_main_flow) {

    @Inject
    lateinit var peopleFeatureApi: PeopleFeatureApi

    @Inject
    lateinit var dialogsFeatureApi: DialogsFeatureApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                add(R.id.mainContainer, dialogsFeatureApi.getContainerFragment())
            }
        }
    }
}