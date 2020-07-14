package ru.sudox.android.dialogs.impl

import androidx.fragment.app.Fragment
import ru.sudox.android.dialogs.api.DialogsFeatureApi

class DialogsFeatureImpl : DialogsFeatureApi {
    override fun getContainerFragment(): Fragment = DialogsFlowFragment()
}