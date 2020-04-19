package ru.sudox.design.appbar.vos.others

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import ru.sudox.design.appbar.R
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.AppBarVO

const val APPBAR_SEARCH_CANCEL_BUTTON_TAG = 3

class SearchAppBarVO : AppBarVO {

    var appCompatEditText: AppCompatEditText? = null

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(
                APPBAR_SEARCH_CANCEL_BUTTON_TAG,
                R.drawable.ic_cancel,
                NOT_USED_PARAMETER,
                iconTint = R.color.appbar_search_cancel_icon_tint)
        )
    }

    override fun getViewAtLeft(context: Context): View? {
        appCompatEditText = AppCompatEditText(context, null, R.attr.appBarSearchEditTextStyle).apply {
            maxLines = 1
            isSingleLine = true
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return appCompatEditText
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return NOT_USED_PARAMETER
    }
}