package ru.sudox.android.main.impl

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.dialogs.api.DialogsFeatureApi
import ru.sudox.android.people.api.PeopleFeatureApi
import javax.inject.Inject

@AndroidEntryPoint
class MainMasterFragment : Fragment(R.layout.fragment_main_master) {

    @Inject
    lateinit var peopleFeatureApi: PeopleFeatureApi

    @Inject
    lateinit var dialogsFeatureApi: DialogsFeatureApi


}