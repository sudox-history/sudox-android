package com.sudox.android.data.models.avatar

import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.avatar.impl.PhotoAvatarInfo

@Deprecated(message = "Создана отдельная View для таких задач по отображение")
abstract class AvatarInfo(val string: String) {

    /**
     * Метод для чтения информации об аватаре
     **/
    abstract fun parse(string: List<String>)

    companion object {

        /**
         * Общий метод для чтения информации (определяет тип и возвращает экземляр аватара)
         **/
        fun parse(string: String): AvatarInfo {
            val split = string.split(".")
            val type = split[0]
            val avatar = if (type == "col") {
                ColorAvatarInfo(string)
            } else {
                PhotoAvatarInfo(string)
            }

            // Read data
            avatar.parse(split)

            // Return instance
            return avatar
        }
    }
}