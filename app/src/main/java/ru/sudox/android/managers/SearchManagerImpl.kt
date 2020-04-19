package ru.sudox.android.managers

import android.app.Activity
import android.os.Bundle
import android.view.View
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.core.managers.SearchManager
import ru.sudox.design.appbar.AppBarLayout
import ru.sudox.design.appbar.vos.others.SearchAppBarVO
import ru.sudox.design.common.hideSoftKeyboard
import ru.sudox.design.common.showSoftKeyboard

private const val SEARCHMANAGER_IS_SEARCH_ENABLED_KEY = "appbarmanager_is_search_enabled_key"
private const val SEARCHMANAGER_SEARCH_EDITTEXT_ID = "appbarmanager_search_edittext_id"

class SearchManagerImpl(
        val appBarLayout: AppBarLayout,
        val appBarManager: AppBarManager,
        val activity: Activity
) : SearchManager {

    override fun toggleSearch(toggle: Boolean, editTextId: Int, callback: ((Int) -> Unit)?) {
        if (toggle == isSearchEnabled()) {
            return
        }

        if (toggle) {
            appBarManager.onlyStoreChanges(true)
            appBarManager.setVO(appBarLayout.appBar!!.vo, appBarLayout.appBar!!.callback)

            val searchVO = SearchAppBarVO(editTextId)

            appBarLayout.appBar!!.vo = searchVO
            appBarLayout.appBar!!.callback = callback
            appBarLayout.post { searchVO.searchEditText!!.showSoftKeyboard() }
        } else {
            val vo = appBarLayout.appBar!!.vo as SearchAppBarVO

            if (vo.searchEditText!!.isFocused) {
                activity.hideSoftKeyboard()
            }

            appBarManager.onlyStoreChanges(false)
        }
    }

    override fun isSearchEnabled(): Boolean {
        return appBarLayout.appBar!!.vo is SearchAppBarVO
    }

    override fun saveSearchState(bundle: Bundle) {
        bundle.putBoolean(SEARCHMANAGER_IS_SEARCH_ENABLED_KEY, isSearchEnabled())

        if (isSearchEnabled()) {
            bundle.putInt(SEARCHMANAGER_SEARCH_EDITTEXT_ID, (appBarLayout.appBar!!.vo as SearchAppBarVO).searchEditText!!.id)
        }
    }

    override fun restoreSearchState(bundle: Bundle?, callback: ((Int) -> Unit)?) {
        if (bundle == null) {
            toggleSearch(false)
        } else {
            val toggle = bundle.getBoolean(SEARCHMANAGER_IS_SEARCH_ENABLED_KEY, false)
            val editTextId = bundle.getInt(SEARCHMANAGER_SEARCH_EDITTEXT_ID, View.NO_ID)

            toggleSearch(toggle, editTextId, callback)
        }
    }
}