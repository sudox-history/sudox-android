package com.sudox.android.data.models.avatar.impl

import com.sudox.android.data.models.avatar.Avatar

class ColorAvatar : Avatar() {

    lateinit var firstColor: String
    lateinit var secondColor: String

    override fun parse(split: List<String>) {
        firstColor = split[1]
        secondColor = split[2]
    }
}