package com.sudox.android.data.models.avatar.impl

import com.sudox.android.data.models.avatar.AvatarInfo

@Deprecated(message = "Создана отдельная View для таких задач по отображение")
class ColorAvatarInfo(string: String) : AvatarInfo(string) {

    lateinit var firstColor: String
    lateinit var secondColor: String

    override fun parse(split: List<String>) {
        firstColor = "#${split[1]}"
        secondColor = "#${split[2]}"
    }
}