package com.sudox.android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.android.R
import dagger.android.AndroidInjection

class AuthActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val splashDataBinding = DataBind

    }
}