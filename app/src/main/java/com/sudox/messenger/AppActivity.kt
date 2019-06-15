package com.sudox.messenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.auth.ui.phone.AuthPhoneFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        supportFragmentManager
                .beginTransaction()
                .add(R.id.appFrameLayout, AuthPhoneFragment())
                .commit()
    }
}