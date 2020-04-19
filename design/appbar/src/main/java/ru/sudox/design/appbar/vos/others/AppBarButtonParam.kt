package ru.sudox.design.appbar.vos.others

const val NOT_USED_PARAMETER = 0

data class AppBarButtonParam(
        var tag: Int,
        var iconRes: Int = NOT_USED_PARAMETER,
        var textRes: Int = NOT_USED_PARAMETER,
        var isEnabled: Boolean = true
)