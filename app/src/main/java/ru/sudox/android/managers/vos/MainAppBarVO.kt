package ru.sudox.android.managers.vos

import android.content.Context
import android.view.View
import ru.sudox.android.AppLoader
import ru.sudox.android.R
import ru.sudox.api.common.SudoxApi
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import javax.inject.Inject

/**
 * ViewObject для AppBar'а с возможностью подмены заголовка.
 *
 * @param originalAppBarVO Оригинальный ViewObject, который будет использоваться если
 * соединение установлено.
 */
class MainAppBarVO(
        var originalAppBarVO: AppBarVO
) : AppBarVO {

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    init {
        AppLoader.loaderComponent?.inject(this)
    }

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return originalAppBarVO.getButtonsAtLeft()
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return originalAppBarVO.getButtonsAtRight()
    }

    override fun getViewAtLeft(context: Context): View? {
        return originalAppBarVO.getViewAtLeft(context)
    }

    override fun getViewAtRight(context: Context): View? {
        return originalAppBarVO.getViewAtRight(context)
    }

    override fun getTitle(): Int {
        return if (sudoxApi!!.isConnected) {
            originalAppBarVO.getTitle()
        } else {
            R.string.connecting
        }
    }
}