package ru.sudox.android.managers

import android.app.Activity
import android.os.Bundle
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.managers.vos.MainAppBarVO
import ru.sudox.api.common.SudoxApi
import ru.sudox.design.appbar.AppBarLayout
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.others.SearchAppBarVO
import ru.sudox.design.common.hideSoftKeyboard
import ru.sudox.design.common.showSoftKeyboard

private const val APPBARMANAGER_IS_SEARCH_ENABLED_KEY = "appbarmanager_is_search_enabled_key"
private const val APPBARMANAGER_SEARCH_EDITTEXT_ID = "appbarmanager_search_edittext_id"

class AppBarManagerImpl(
        val activity: Activity,
        val appBarLayout: AppBarLayout,
        val sudoxApi: SudoxApi
) : AppBarManager {

    private var apiStatusDisposable: Disposable? = null
    private var savedAppBarCallback: ((Int) -> Unit)? = null
    private var savedAppBarVO: AppBarVO? = null

    override fun onStart() {
        apiStatusDisposable = sudoxApi
                .statusSubject
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe { setVO(appBarLayout.appBar!!.vo, appBarLayout.appBar!!.callback) }
    }

    override fun onStop() {
        if (apiStatusDisposable?.isDisposed == false) {
            apiStatusDisposable?.dispose()
        }
    }

    override fun setVO(vo: AppBarVO?, callback: ((Int) -> Unit)?, force: Boolean) {
        if (isSearchEnabled() && !force) {
            savedAppBarCallback = callback
            savedAppBarVO = vo
            return
        }

        val realVo = if (vo is MainAppBarVO) {
            vo.originalAppBarVO
        } else {
            vo
        }

        appBarLayout.appBar!!.let {
            it.callback = callback
            it.vo = if (realVo != null) {
                MainAppBarVO(realVo)
            } else {
                null
            }
        }
    }

    override fun toggleLoading(toggle: Boolean) {
        val vo = appBarLayout.appBar!!.vo

        if (vo is MainAppBarVO) {
            vo.disableButtons = toggle
        }

        appBarLayout.appBar!!.vo = vo
    }

    override fun toggleSearch(toggle: Boolean, editTextId: Int, callback: ((Int) -> Unit)?) {
        if (toggle == isSearchEnabled()) {
            return
        }

        if (toggle) {
            onStop()

            savedAppBarVO = appBarLayout.appBar!!.vo
            savedAppBarCallback = appBarLayout.appBar!!.callback

            val searchVO = SearchAppBarVO(editTextId)

            appBarLayout.appBar!!.vo = searchVO
            appBarLayout.appBar!!.callback = callback
            appBarLayout.post { searchVO.searchEditText!!.showSoftKeyboard() }
        } else {
            val vo = appBarLayout.appBar!!.vo as SearchAppBarVO

            if (vo.searchEditText!!.isFocused) {
                activity.hideSoftKeyboard()
            }

            val oldSavedAppBarCallback = savedAppBarCallback
            val oldSavedAppBarVO = savedAppBarVO

            // Предотвращаем утечки памяти
            savedAppBarCallback = null
            savedAppBarVO = null

            setVO(oldSavedAppBarVO, oldSavedAppBarCallback, true)

            // Начинаем заново слушать обновления состояния API
            onStart()
        }
    }

    override fun saveSearchState(bundle: Bundle) {
        bundle.putBoolean(APPBARMANAGER_IS_SEARCH_ENABLED_KEY, isSearchEnabled())

        if (isSearchEnabled()) {
            bundle.putInt(APPBARMANAGER_SEARCH_EDITTEXT_ID, (appBarLayout.appBar!!.vo as SearchAppBarVO).searchEditText!!.id)
        }
    }

    override fun restoreSearchState(bundle: Bundle?, callback: ((Int) -> Unit)?) {
        if (bundle == null) {
            toggleSearch(false)
        } else {
            val toggle = bundle.getBoolean(APPBARMANAGER_IS_SEARCH_ENABLED_KEY, false)
            val editTextId = bundle.getInt(APPBARMANAGER_SEARCH_EDITTEXT_ID, View.NO_ID)

            toggleSearch(toggle, editTextId, callback)
        }
    }

    override fun isSearchEnabled(): Boolean {
        return appBarLayout.appBar!!.vo is SearchAppBarVO
    }

    override fun setLayoutVO(vo: AppBarLayoutVO?) {
        appBarLayout.vo = vo
    }
}