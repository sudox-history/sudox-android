package com.sudox.messenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.design.widgets.navbar.NavigationBar
import com.sudox.messenger.auth.ui.phone.AuthPhoneFragment
import com.sudox.messenger.core.AppActivity
import kotlinx.android.synthetic.main.activity_app.*

class AppActivityImpl : AppCompatActivity(), AppActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        supportFragmentManager
                .beginTransaction()
                .add(R.id.appFrameLayout, AuthPhoneFragment())
                .commit()
    }

    override fun getNavigationBar(): NavigationBar {
        return appNavigationBar
    }
}