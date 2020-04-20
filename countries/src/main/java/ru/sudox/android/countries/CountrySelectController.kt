package ru.sudox.android.countries

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.design.viewlist.ViewList

const val COUNTRY_EXTRA_NAME = "country"
const val COUNTRY_CHANGE_REQUEST_CODE = 1

class CountrySelectController : ViewListController<CountrySelectAdapter>() {

    private var viewModel: CountrySelectViewModel? = null

    init {
        appBarVO = CountrySelectAppBarVO()
    }

    override fun bindView(view: View) {
        super.bindView(view)

        viewModel = getViewModel()
        viewModel!!.apply {
            searchLiveData.observe(this@CountrySelectController, Observer {
                if (it != null) {
                    if (adapter!!.countries != null) {
                        DiffUtil
                                .calculateDiff(CountrySelectDiffCallback(adapter!!.countries!!, it))
                                .dispatchUpdatesTo(adapter!!)
                    }

                    adapter!!.countries = it
                }
            })

            countriesLiveData.observe(this@CountrySelectController, Observer {
                adapter!!.countries = it
                adapter!!.notifyDataSetChanged()
            })

            loadCountries(activity!!, false)
        }
    }

    override fun getAdapter(viewList: ViewList): CountrySelectAdapter {
        return CountrySelectAdapter(activity!!) {
            navigationManager!!.popBackstack()
            targetController!!.onActivityResult(COUNTRY_CHANGE_REQUEST_CODE, 0, Intent().apply {
                putExtra(COUNTRY_EXTRA_NAME, it)
            })
        }
    }

    override fun onSearchRequest(text: String?) {
        if (text != null) {
            viewModel!!.searchStartsWith(activity!!, text)
        } else {
            viewModel!!.loadCountries(activity!!, true)
        }
    }
}