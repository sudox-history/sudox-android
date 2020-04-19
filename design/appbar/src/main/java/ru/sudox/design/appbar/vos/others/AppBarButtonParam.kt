package ru.sudox.design.appbar.vos.others

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.sudox.design.appbar.R

const val NOT_USED_PARAMETER = 0

data class AppBarButtonParam(
        var tag: Int,
        @DrawableRes var iconRes: Int = NOT_USED_PARAMETER,
        @StringRes var textRes: Int = NOT_USED_PARAMETER,
        var isEnabled: Boolean = true,
        @ColorRes var iconTint: Int = R.color.appbar_button_icon_tint
)