package ru.sudox.android.dialogs.impl.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dialogs_search.*
import ru.sudox.android.core.ui.setupWithFragmentManager
import ru.sudox.android.dialogs.impl.R

/**
 * Фрагмент поиска диалогов.
 */
class DialogsSearchFragment : Fragment(R.layout.fragment_dialogs_search) {

    private val viewModel by viewModels<DialogsSearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DialogsSearchAdapter(requireContext(), this)

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
            if (it.chatsItems.isNotEmpty() && !adapter.sections.containsKey(FOUND_CHAT_SECTION_ORDER)) {
                adapter.addSection(FOUND_CHAT_SECTION_ORDER, viewModel.chatsSectionVO)
            } else if (it.chatsItems.isEmpty() && adapter.sections.containsKey(FOUND_CHAT_SECTION_ORDER)) {
                adapter.removeSection(FOUND_CHAT_SECTION_ORDER)
            }

            adapter.changeSectionItems(FOUND_CHAT_SECTION_ORDER, it.chatsItems, false)
        })

        dialogsSearchToolbar.setupWithFragmentManager(requireActivity(), parentFragmentManager)
        dialogsSearchList.layoutManager = LinearLayoutManager(context)
        dialogsSearchList.adapter = adapter
    }
}