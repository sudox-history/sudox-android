package ru.sudox.android.dialogs.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint

/**
 * Фрагмент-контейнер для модуля диалогов.
 */
@AndroidEntryPoint
class DialogsFlowFragment : Fragment(R.layout.fragment_dialogs_flow) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                add(R.id.dialogsFlowContainer, DialogsTabsFragment())
            }
        }
    }
}