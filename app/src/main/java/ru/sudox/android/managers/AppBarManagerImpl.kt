package ru.sudox.android.managers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.managers.vos.MainAppBarVO
import ru.sudox.api.common.SudoxApi
import ru.sudox.design.appbar.CustomAppBarLayout
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO

class AppBarManagerImpl(
        val appBarLayout: CustomAppBarLayout,
        val sudoxApi: SudoxApi
) : AppBarManager {

    private var onlyStoreChanges = false
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

    override fun onlyStoreChanges(toggle: Boolean) {
        onlyStoreChanges = toggle

        if (toggle) {
            onStop()

            savedAppBarCallback = appBarLayout.appBar!!.callback
            savedAppBarVO = appBarLayout.appBar!!.vo
        } else {
            val oldSavedAppBarCallback = savedAppBarCallback
            val oldSavedAppBarVO = savedAppBarVO

            // Предотвращаем утечки памяти
            savedAppBarCallback = null
            savedAppBarVO = null

            setVO(oldSavedAppBarVO, oldSavedAppBarCallback, true)
            onStart()
        }
    }

    override fun setVO(vo: AppBarVO?, callback: ((Int) -> Unit)?, force: Boolean) {
        if (onlyStoreChanges && !force) {
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

    override fun requestElevationToggling(toggle: Boolean, animate: Boolean) {
        if (appBarLayout.childCount > 1) {
            appBarLayout.requestStrokeShowing(true, animate = false)
        } else {
            appBarLayout.requestStrokeShowing(toggle, animate = animate)
        }
    }

    override fun toggleLoading(toggle: Boolean) {
        val vo = appBarLayout.appBar!!.vo

        if (vo is MainAppBarVO) {
            vo.disableButtons = toggle
        }

        appBarLayout.appBar!!.vo = vo
    }

    override fun setLayoutVO(vo: AppBarLayoutVO?) {
        appBarLayout.vo = vo
    }
}