package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.android.auth.phone.AuthPhoneFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreLoader
import com.sudox.messenger.android.core.managers.ApplicationBarManager
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager
import com.sudox.messenger.android.managers.AppApplicationBarManager
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager
import kotlinx.android.synthetic.main.activity_app.applicationBar
import kotlinx.android.synthetic.main.activity_app.navigationBar

class AppActivity : AppCompatActivity(), CoreActivity {

    private val screenManager = AppScreenManager(this)
    private val navigationManager by lazy {
        AppNavigationManager(this, supportFragmentManager, navigationBar, R.id.frameContainer)
    }

    private val applicationBarManager by lazy {
        AppApplicationBarManager(applicationBar, navigationManager)
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

    override fun onBackPressed() {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onBackPressed()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationManager.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun getScreenManager(): ScreenManager {
        return screenManager
    }

    override fun getNavigationManager(): NavigationManager {
        return navigationManager
    }

    override fun getApplicationBarManager(): ApplicationBarManager {
        return applicationBarManager
    }

    override fun getLoader(): CoreLoader {
        return application as CoreLoader
    }
}