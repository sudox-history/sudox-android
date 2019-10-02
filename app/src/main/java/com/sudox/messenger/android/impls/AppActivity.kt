package com.sudox.messenger.android.impls

import android.app.Activity
import android.os.Bundle
import com.sudox.design.drawables.GradientBackgroundDrawable

class AppActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(GradientBackgroundDrawable(this))

        super.onCreate(savedInstanceState)
    }
}