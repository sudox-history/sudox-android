package ru.sudox.android.countries.impl

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_countries.*
import ru.sudox.android.core.ui.applyInserts
import ru.sudox.android.core.ui.setupWithFragmentManager
import ru.sudox.android.core.ui.toolbar.helpers.setupWithRecyclerView
import ru.sudox.android.countries.impl.list.CountryLetterBinder

/**
 * Фрагмент списка стран.
 */
@AndroidEntryPoint
class CountriesFragment : Fragment(R.layout.fragment_countries), SearchView.OnQueryTextListener {

    private val viewModel: CountriesViewModel by viewModels()
    private var flagHeight = 0
    private var flagWidth = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CountriesAdapter(viewModel::getName, ::getResizedFlag) {}

        countriesToolbar.setupWithFragmentManager(requireActivity(), parentFragmentManager)
        (countriesToolbar.menu.findItem(R.id.countriesSearchItem).actionView as SearchView).setOnQueryTextListener(this)

        flagHeight = requireContext().resources.getDimensionPixelSize(R.dimen.countriesListFlagHeight)
        flagWidth = requireContext().resources.getDimensionPixelSize(R.dimen.countriesListFlagWidth)

        countriesList.adapter = adapter
        countriesList.layoutManager = LinearLayoutManager(context)
        countriesList.applyInserts(top = false, bottom = true)
        countriesListContainer.enableStickyViews(COUNTRY_LETTER_VIEW_TYPE, CountryLetterBinder(viewModel::getName))
        countriesAppBarLayout.setupWithRecyclerView(countriesList)
        countriesListContainer.toggleLoading(true)

        viewModel.countriesLiveData.observe(viewLifecycleOwner, Observer {
            countriesListContainer.toggleLoading(false)
            adapter.changeItems(it.second, false)

            if (it.first) {
                countriesList.scrollToPosition(0)
            }
        })

        viewModel.load()
    }

    private fun getResizedFlag(code: String): Drawable = viewModel.getFlag(code).mutate().apply {
        setBounds(0, 0, flagWidth, flagHeight)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.search(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean = false
}