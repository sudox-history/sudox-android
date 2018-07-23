package com.sudox.android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sudox.android.R
import com.sudox.android.vm.SplashViewModel
import dagger.android.AndroidInjection

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.getData().observe(this, Observer {
            TODO("Go to AuthActivity or MainActivity")
        })
        viewModel.connect()
    }
}