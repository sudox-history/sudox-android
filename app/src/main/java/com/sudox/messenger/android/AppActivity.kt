package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.android.auth.AuthPhoneFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.managers.ApplicationBarManager
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.managers.AppApplicationBarManager
import com.sudox.messenger.android.managers.AppNavigationManager
import kotlinx.android.synthetic.main.activity_app.applicationBar

class AppActivity : AppCompatActivity(), CoreActivity {

    private val navigationManager = AppNavigationManager(supportFragmentManager, R.id.frameContainer)
    private val applicationBarManager by lazy {
        AppApplicationBarManager(applicationBar, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        setContentView(R.layout.activity_app)

        if (savedInstanceState != null) {
            navigationManager.restoreState(savedInstanceState)
        } else {
            navigationManager.showFragment(AuthPhoneFragment(), false)
        }
    }

    override fun onBackPressed() {
        if (!navigationManager.showPreviousFragment()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationManager.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun getNavigationManager(): NavigationManager {
        return navigationManager
    }

    override fun getApplicationBarManager(): ApplicationBarManager {
        return applicationBarManager
    }
}