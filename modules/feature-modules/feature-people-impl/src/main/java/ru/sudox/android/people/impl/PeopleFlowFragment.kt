package ru.sudox.android.people.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint

/**
 * Фрагмент-контейнер для экрана People
 */
@AndroidEntryPoint
class PeopleFlowFragment : Fragment(R.layout.fragment_people_flow) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                add(R.id.peopleFlowContainer, PeopleTabsFragment())
            }
        }
    }
}