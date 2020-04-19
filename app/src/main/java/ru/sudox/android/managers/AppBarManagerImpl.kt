package ru.sudox.android.managers

import android.app.Activity
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

    override fun setVO(vo: AppBarVO?, callback: ((Int) -> Unit)?) {
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

    override fun toggleSearch(toggle: Boolean) {
        if (toggle) {
            onStop()

            savedAppBarVO = appBarLayout.appBar!!.vo
            savedAppBarCallback = appBarLayout.appBar!!.callback

            val searchVO = SearchAppBarVO()

            appBarLayout.appBar!!.vo = searchVO
            appBarLayout.post { searchVO.appCompatEditText!!.showSoftKeyboard() }
        } else {
            val vo = appBarLayout.appBar!!.vo as SearchAppBarVO

            if (vo.appCompatEditText!!.isFocused) {
                activity.hideSoftKeyboard()
            }

            setVO(savedAppBarVO, savedAppBarCallback)

            // Предотвращаем утечки памяти
            savedAppBarCallback = null
            savedAppBarVO = null

            // Начинаем заново слушать обновления состояния API
            onStart()
        }
    }

    override fun isSearchEnabled(): Boolean {
        return savedAppBarVO != null
    }

    override fun setLayoutVO(vo: AppBarLayoutVO?) {
        appBarLayout.vo = vo
    }
}