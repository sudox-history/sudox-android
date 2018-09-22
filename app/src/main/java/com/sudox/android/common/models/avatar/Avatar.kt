package com.sudox.android.common.models.avatar

import com.sudox.android.common.models.avatar.impl.ColorAvatar
import com.sudox.android.common.models.avatar.impl.PhotoAvatar

abstract class Avatar {

    // Метод для чтения информации об аватаре
    abstract fun parse(string: List<String>)

    companion object {

        // Общий метод для чтения информации (определяет тип и возвращает экземляр аватара)
        fun parse(string: String): Avatar {
            val split = string.split(".")
            val type = split[0]
            val avatar = if (type == "col") ColorAvatar() else PhotoAvatar()

            // Read data
            avatar.parse(split)

            // Return instance
            return avatar
        }
    }
}