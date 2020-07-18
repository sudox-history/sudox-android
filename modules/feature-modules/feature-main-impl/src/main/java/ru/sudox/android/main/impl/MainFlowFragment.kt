package ru.sudox.android.main.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint

/**
 * Фрагмент-контейнер основной части приложения.
 */
@AndroidEntryPoint
class MainFlowFragment : Fragment(R.layout.fragment_main_flow) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                add(R.id.mainFlowContainer, MainMasterFragment())
            }
        }
    }
}