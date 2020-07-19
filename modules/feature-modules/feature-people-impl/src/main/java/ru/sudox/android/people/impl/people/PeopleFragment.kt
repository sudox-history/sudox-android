package ru.sudox.android.people.impl.people

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_people.*
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.adapters.PeopleAdapter
import ru.sudox.android.people.impl.people.adapters.PeopleMaybeYouKnowAdapter
import ru.sudox.simplelists.model.BasicListItem

/**
 * Фрагмент экрана People.
 */
@AndroidEntryPoint
class PeopleFragment : Fragment(R.layout.fragment_people) {

    private val viewModel by viewModels<PeopleViewModel>()
    private lateinit var adapter: PeopleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val maybeYouKnowAdapter = PeopleMaybeYouKnowAdapter(this) {}
        adapter = PeopleAdapter(requireContext(), this, maybeYouKnowAdapter, {}, {})

        if (savedInstanceState != null) {
            adapter.restoreInstanceState(savedInstanceState)
        }

        adapter.loadSectionItems = {
            if (it.order == MAYBE_YOU_KNOW_SECTION_ORDER && !it.isCollapsed) {
                listOf(BasicListItem(MAYBE_YOU_KNOW_SECTION_ORDER, null))
            } else if (it.order == SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER && it.selectedTypeId == R.id.subscribesItem) {
                if (viewModel.subscribesLiveData.value != null && viewModel.subscribesLiveData.value!!.isNotEmpty()) {
                    viewModel.subscribesLiveData.value
                } else {
                    viewModel.loadSubscribes()
                    null
                }
            } else if (it.order == SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER && it.selectedTypeId == R.id.subscriptionsItem) {
                if (viewModel.subscriptionsLiveData.value != null && viewModel.subscriptionsLiveData.value!!.isNotEmpty()) {
                    viewModel.subscriptionsLiveData.value
                } else {
                    viewModel.loadSubscriptions()
                    null
                }
            } else {
                null
            }
        }

        peopleList.setHasFixedSize(true)
        peopleList.layoutManager = LinearLayoutManager(context)
        peopleList.adapter = adapter
        peopleListContainer.toggleLoading(true)

        viewModel.requestsLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                if (!adapter.sections.containsKey(REQUESTS_SECTION_ORDER)) {
                    adapter.addSection(REQUESTS_SECTION_ORDER, viewModel.requestsSectionVO)
                }

                adapter.changeSectionItems(REQUESTS_SECTION_ORDER, it, false)
            } else if (adapter.sections.containsKey(REQUESTS_SECTION_ORDER)) {
                adapter.removeSection(REQUESTS_SECTION_ORDER)
            }
        })

        viewModel.maybeYouKnowLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                if (!adapter.sections.containsKey(MAYBE_YOU_KNOW_SECTION_ORDER)) {
                    adapter.addSection(MAYBE_YOU_KNOW_SECTION_ORDER, viewModel.maybeYouKnowSectionVO)
                }

                maybeYouKnowAdapter.changeItems(it, false)
            } else if (adapter.sections.containsKey(MAYBE_YOU_KNOW_SECTION_ORDER)) {
                adapter.removeSection(MAYBE_YOU_KNOW_SECTION_ORDER)
            }
        })

        viewModel.subscribesLiveData.observe(viewLifecycleOwner, Observer {
            if (viewModel.subscribesAndSubscriptionsSectionVO.selectedTypeId == R.id.subscribesItem) {
                adapter.changeSectionItems(SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER, it, false)
            }

            // Снимаем загрузку ...
            peopleListContainer.toggleLoading(false)
        })

        viewModel.subscriptionsLiveData.observe(viewLifecycleOwner, Observer {
            if (viewModel.subscribesAndSubscriptionsSectionVO.selectedTypeId == R.id.subscriptionsItem) {
                adapter.changeSectionItems(SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER, it, false)
            }
        })

        adapter.addSection(SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER, viewModel.subscribesAndSubscriptionsSectionVO)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        adapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}