package com.sudox.messenger.android.impls

import android.app.Activity
import android.os.Bundle
import com.sudox.messenger.android.R

class AppActivityImpl : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }
}