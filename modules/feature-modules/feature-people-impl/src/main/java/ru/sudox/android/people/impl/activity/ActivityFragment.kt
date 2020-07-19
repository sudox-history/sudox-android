package ru.sudox.android.people.impl.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity.*
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.adapters.ActivityAdapter
import ru.sudox.android.people.impl.activity.adapters.StoriesAdapter
import ru.sudox.simplelists.model.BasicListItem

/**
 * Фрагмент вкладки Activity
 */
@AndroidEntryPoint
class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private val viewModel by viewModels<ActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val storiesAdapter = StoriesAdapter(requireContext(), this)
        val adapter = ActivityAdapter(requireContext(), storiesAdapter, this)

        storiesAdapter.addSection(MY_STORY_SECTION_ORDER)
        storiesAdapter.addSection(OTHER_STORY_SECTION_ORDER)

        activityList.setHasFixedSize(true)
        activityList.layoutManager = LinearLayoutManager(requireContext())
        activityList.adapter = adapter

        // Загружаемся до первой истории ...
        activityListContainer.toggleLoading(true)
        var myStoryInserted = false

        viewModel.myStoryLiveData.observe(viewLifecycleOwner, Observer {
            activityListContainer.toggleLoading(false)

            if (myStoryInserted) {
                storiesAdapter.updateSectionItem(MY_STORY_SECTION_ORDER, 0, it)
            } else {
                storiesAdapter.addItemsToSection(MY_STORY_SECTION_ORDER, 0, listOf(it))
                myStoryInserted = true
            }
        })

        viewModel.storiesLiveData.observe(viewLifecycleOwner, Observer {
            storiesAdapter.changeSectionItems(OTHER_STORY_SECTION_ORDER, it, false)
        })

        viewModel.postsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.changeSectionItems(NEWS_SECTION_ORDER, it, false)
        })

        adapter.addSection(0)
        adapter.addSection(NEWS_SECTION_ORDER, viewModel.newsSectionVO)
        adapter.addItemsToSection(0, 0, listOf(BasicListItem(STORIES_SECTION_ORDER, null)))
    }
}