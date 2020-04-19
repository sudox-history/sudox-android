package ru.sudox.android.managers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.managers.vos.MainAppBarVO
import ru.sudox.api.common.SudoxApi
import ru.sudox.design.appbar.AppBarLayout
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO

class AppBarManagerImpl(
        val appBarLayout: AppBarLayout,
        val sudoxApi: SudoxApi
) : AppBarManager {

    private var apiStatusDisposable: Disposable? = null

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
        appBarLayout.appBar!!.let {
            it.callback = callback
            it.vo = if (vo != null) {
                MainAppBarVO(vo)
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

    override fun setLayoutVO(vo: AppBarLayoutVO?) {
        appBarLayout.vo = vo
    }
}