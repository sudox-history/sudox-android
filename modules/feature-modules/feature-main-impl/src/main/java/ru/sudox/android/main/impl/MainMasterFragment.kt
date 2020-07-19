package ru.sudox.android.main.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main_master.*
import ru.sudox.android.core.ui.navigation.ContainerFragment
import ru.sudox.android.core.ui.navigation.setupWithFragmentManager
import ru.sudox.android.dialogs.api.DialogsFeatureApi
import ru.sudox.android.people.api.PeopleFeatureApi
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainMasterFragment : Fragment(R.layout.fragment_main_master), ContainerFragment {

    private var backstack = Stack<Int>()

    @Inject
    lateinit var peopleFeatureApi: PeopleFeatureApi

    @Inject
    lateinit var dialogsFeatureApi: DialogsFeatureApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainBottomNavigation.setupWithFragmentManager(backstack, R.id.mainMasterContainer, childFragmentManager) {
            when (it) {
                R.id.peopleItem -> peopleFeatureApi.getContainerFragment()
                R.id.messagesItem -> dialogsFeatureApi.getContainerFragment()
                else -> peopleFeatureApi.getContainerFragment()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }
}