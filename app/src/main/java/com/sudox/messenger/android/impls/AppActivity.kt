package com.sudox.messenger.android.impls

import android.app.Activity
import android.os.Bundle
import com.sudox.design.initDesign
import com.sudox.messenger.android.R
import kotlinx.android.synthetic.main.activity_app.phoneEditText

class AppActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        initDesign(this)

        phoneEditText.setCountry("RU", "7", com.sudox.design.R.drawable.ic_flag_russia)
    }
}