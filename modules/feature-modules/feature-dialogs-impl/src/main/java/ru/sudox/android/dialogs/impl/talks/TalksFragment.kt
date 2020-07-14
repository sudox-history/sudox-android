package ru.sudox.android.dialogs.impl.talks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dialogs_tab.*
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.adapters.DialogsAdapter

@AndroidEntryPoint
class TalksFragment : Fragment(R.layout.fragment_dialogs_tab) {

    private val viewModel: TalksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DialogsAdapter({}, this)

        dialogsList.adapter = adapter
        dialogsList.layoutManager = LinearLayoutManager(context)
        dialogsListContainer.toggleLoading(true)

        viewModel.talksLiveData.observe(viewLifecycleOwner, Observer {
            adapter.changeItems(it, false)
            dialogsListContainer.toggleLoading(false)
        })
    }
}