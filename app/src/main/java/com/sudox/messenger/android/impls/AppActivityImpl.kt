package com.sudox.messenger.android.impls

import android.os.Bundle
import com.sudox.design.StyledActivity
import com.sudox.messenger.android.R

class AppActivityImpl : StyledActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }
}